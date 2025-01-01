package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.days.Day24.Gate
import me.reckter.aoc.days.Day24.Operation.AND
import me.reckter.aoc.days.Day24.Operation.OR
import me.reckter.aoc.days.Day24.Operation.XOR
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.toIntegers
import kotlin.math.max

class Day24 : Day {
    override val day = 24

    data class Gate(
        val a: String,
        val b: String,
        val result: String,
        val operation: Operation,
    ) {
        fun calc(values: Map<String, Boolean>): Boolean? {
            val aVal = values[a] ?: return null
            val bVal = values[b] ?: return null

            return operation.calc(aVal, bVal)
        }

        fun getTopoSortValue(values: Map<String, Int>): Int? {
            val aVal = values[a] ?: return null
            val bVal = values[b] ?: return null

            return max(aVal, bVal) + 1
        }
    }

    enum class Operation(
        val calc: (a: Boolean, c: Boolean) -> Boolean,
    ) {
        AND({ a, b -> a && b }),
        OR({ a, b -> a || b }),
        XOR({ a, b -> a xor b }),
    }

    fun calculateAllValues(
        initialVales: Map<String, Boolean>,
        gates: List<Gate>,
    ): Map<String, Boolean> {
        val values = initialVales.toMutableMap()
        val queue = ArrayDeque(gates)
        while (queue.size > 0) {
            val next = queue.removeFirst()

            val result = next.calc(values)
            if (result == null) {
                queue.add(next)
                continue
            }

            values[next.result] = result
        }
        return values
    }

    fun getGatesByTopoLayers(
        initialVales: Map<String, Boolean>,
        dependents: Map<String, List<String>>,
    ): Map<Int, List<String>> {
        val values =
            initialVales
                .mapValues { 0 }
                .toMutableMap()
        val queue = ArrayDeque(gates)

        var changes = true

        while (changes) {
            changes = false
            dependents
                .forEach { (gate, dependents) ->
                    val value = values[gate]
                    if (value != null) {
                        dependents
                            .forEach { dependent ->
                                if ((values[dependent] ?: -1) < value + 1) {
                                    values[dependent] = value + 1
                                    changes = true
                                }
                            }
                    }
                    val maxVal =
                        dependents
                            .mapNotNull { values[it] }
                            .maxOrNull()
                    if (maxVal != null && !gate.startsWith("x") && !gate.startsWith("y")) {
                        values[gate] = maxVal - 1
                    }
                }
        }

        return values
            .entries
            .groupBy { it.value }
            .mapValues { it.value.map { it.key } }
    }

    val initialValues by lazy {
        loadInput()
            .parseWithRegex("(.*?): (1|0)")
            .map { (name, boolStr) ->
                name to if (boolStr == "1") true else false
            }.toMap()
    }

    val gates by lazy {
        loadInput()
            .parseWithRegex("(.*?) (AND|OR|XOR) (.*?) -> (.*?)")
            .map { (a, op, b, res) ->
                Gate(
                    a,
                    b,
                    res,
                    when (op) {
                        "AND" -> AND
                        "OR" -> OR
                        "XOR" -> XOR
                        else -> error("no op $op")
                    },
                )
            }
    }

    override fun solvePart1() {
        val results =
            calculateAllValues(initialValues, gates)
                .entries

        val num =
            results
                .filter { it.key.startsWith("z") }
                .sortedBy { -it.key.removePrefix("z").toInt() }
                .joinToString("") { if (it.value) "1" else "0" }

        num
            .toLong(2)
            .solution(1)
    }

    fun findSubstitution(
        start: Map<String, String>,
        pattern: List<Gate>,
        gates: List<Gate>,
    ): Map<String, String> {
        val substitution = start.toMutableMap()

        var changed = true
        while (changed) {
            changed = false
            gates
                .forEach { gate ->
                    val aReplacement = substitution[gate.a]
                    val bReplacment = substitution[gate.b]
                    if (aReplacement != null && bReplacment != null && !substitution.contains(gate.result)) {
                        val sub =
                            pattern
                                .filter { it.operation == gate.operation }
                                .find {
                                    (it.a == aReplacement && it.b == bReplacment) ||
                                        (it.b == aReplacement && it.a == bReplacment)
                                }

                        if (sub != null && sub.result.startsWith("z") == gate.result.startsWith("z")) {
                            substitution[gate.result] = sub.result
                            changed = true
                        }
                    }
                }
            gates
                .forEach { gate ->
                    val resultReplacement = substitution[gate.result]
                    if (resultReplacement != null) {
                        val existing =
                            listOf(gate.a, gate.b)
                                .mapNotNull { sub -> substitution[sub]?.let { sub to it } }
                        if (existing.size == 1) {
                            val sub =
                                pattern
                                    .filter { it.operation == gate.operation }
                                    .find {
                                        it.result == resultReplacement &&
                                            (it.a == existing.first().second || it.b == existing.first().second)
                                    }

                            if (sub != null) {
                                val subTo =
                                    listOf(sub.a, sub.b).find { it != existing.first().second }
                                        ?: error("no subTo found")
                                val subFrom =
                                    listOf(gate.a, gate.b).find { it != existing.first().first }
                                        ?: error("no subFrom Found")
                                substitution[subFrom] = subTo
                                changed = true
                            }
                        }
                    }
                }
        }
        return substitution
    }

    fun printGraph(list: List<Gate>) {
        println("digraph {")

        list
            .map {
                "${it.a} -> ${it.result};\n ${it.b} -> ${it.result}; ${it.result} [label=\"${it.result} ${it.operation}\" ${
                    if (it.result.startsWith(
                            "z",
                        )
                    ) {
                        "style=filled,color=\".7 .3 1.0\""
                    } else {
                        ""
                    }
                }];"
            }.joinToString("\n")
            .let { println(it) }
        println("}")
    }

    override fun solvePart2() {
        val dependents =
            gates
                .flatMap {
                    listOf(it.a to it.result, it.b to it.result)
                }.groupBy { it.first }
                .mapValues { it.value.map { it.second } }

        val sorted =
            getGatesByTopoLayers(initialValues, dependents)
                .mapValues {
                    it.value.map { entry ->
                        val gate = gates.find { it.result == entry }

                        if (gate != null) {
                            "${gate.result} = ${gate.a} ${gate.operation} ${gate.b} "
                        } else {
                            entry
                        }
                    }
                }.entries
                .sortedBy { it.key }

        val endGates =
            gates
                .filter { it.result.startsWith("z") }
                .map { it.result.removePrefix("z") }
                .toIntegers()
                .sorted()

        val bigPattern =
            endGates
                .dropLast(1)
                .flatMap { i ->
                    val suffix = "_" + i.toString().padStart(2, '0')
                    val pattern =
                        when (i) {
                            endGates.first() -> startPattern
                            endGates.dropLast(1).last() -> endPattern
                            else -> midPattern
                        }

                    patternForTurn(pattern, i)
                }

        val startSubstitution =
            endGates
                .flatMap { i ->
                    val suffix = "_" + i.toString().padStart(2, '0')
                    val startSubstitution =
                        listOfNotNull(
                            "x${i.toString().padStart(2, '0')}" to "x$suffix",
                            "y${i.toString().padStart(2, '0')}" to "y$suffix",
                            "z${i.toString().padStart(2, '0')}" to "z$suffix",
                        ).toMap()

                    startSubstitution
                        .entries
                        .map { it.key to it.value }
                }.toMap()

        val substitutions = findSubstitution(startSubstitution, bigPattern, gates)

        val wrongFromMatches = mutableSetOf<String>()

        gates
            .forEach { gate ->
                val resultReplacement = substitutions[gate.result]
                if (resultReplacement != null) {
                    val existing =
                        listOf(gate.a, gate.b)
                            .mapNotNull { sub -> substitutions[sub]?.let { sub to it } }
                    val sub =
                        bigPattern
                            .filter { it.operation == gate.operation }
                            .find {
                                it.result == resultReplacement
                            }

                    if (sub != null) {
                        val subArguments =
                            listOf(sub.a, sub.b)
                                .sorted()
                        val args =
                            listOf(gate.a, gate.b)
                                .mapNotNull { substitutions[it] }
                                .sorted()
                        if (subArguments != args) {
                            val wrongArgs = args - subArguments

                            wrongArgs
                                .forEach { arg ->
                                    val sub = substitutions.entries.find { it.value == arg }?.key
                                    if (sub != null) wrongFromMatches.add(sub)
                                }
                        }
                    }
                }
            }

        gates
            .mapNotNull { gate ->
                val aReplacement = substitutions[gate.a]
                val bReplacment = substitutions[gate.b]
                if (aReplacement != null && bReplacment != null) {
                    val sub =
                        bigPattern
                            .filter { it.operation == gate.operation }
                            .find {
                                (it.a == aReplacement && it.b == bReplacment) ||
                                    (it.b == aReplacement && it.a == bReplacment)
                            }

                    if (sub != null && sub.result != substitutions[gate.result]) { // && (!sub.result.startsWith("oo") && !sub.result.startsWith( "oi" )) ) {

                        val shouldBeSubstitution = substitutions.entries.find { it.value == sub.result }?.key
                        gate.result to sub.result
                    } else {
                        null
                    }
                } else {
                    null
                }
            }.map { it.first }
            .let { it + wrongFromMatches }
            .sorted()
            .joinToString(",")
            .solution(2)
    }
}

fun main() = solve<Day24>()

val y01 = true
val x01 = false
val o00 = true

val startPattern =
    listOf(
        Gate("x", "y", "z", XOR),
        Gate("x", "y", "oo", AND),
    )
val midPattern =
    listOf(
        Gate("x", "y", "tmp1", XOR),
        Gate("x", "y", "tmp2", AND),
        Gate("oi", "tmp1", "tmp3", AND),
        Gate("tmp1", "oi", "z", XOR),
        Gate("tmp2", "tmp3", "oo", OR),
    )
val endPattern =
    listOf(
        Gate("x", "y", "tmp1", XOR),
        Gate("x", "y", "tmp2", AND),
        Gate("oi", "tmp1", "tmp3", AND),
        Gate("tmp1", "oi", "z", XOR),
        Gate("tmp2", "tmp3", "z+1", OR),
    )

fun getNameForPattern(
    orig: String,
    suffix: String,
    i: Int,
): String {
    if (orig == "oi") return "oo_${(i - 1).toString().padStart(2, '0')}"
    if (orig == "z+1") return "z_${(i + 1).toString().padStart(2, '0')}"
    return orig + suffix
}

fun patternForTurn(
    pattern: List<Gate>,
    i: Int,
): List<Gate> {
    val suffix = "_" + i.toString().padStart(2, '0')
    return pattern
        .map {
            it.copy(
                a = getNameForPattern(it.a, suffix, i),
                b = getNameForPattern(it.b, suffix, i),
                result = getNameForPattern(it.result, suffix, i),
            )
        }
}

val tmp1 = x01 xor y01
val tmp2 = x01 and y01
val tmp3 = o00 and tmp1

val z01 = tmp1 xor o00
var o01 = tmp2 or tmp3
