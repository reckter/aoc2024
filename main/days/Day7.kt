package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.days.Day7.Operation.Add
import me.reckter.aoc.days.Day7.Operation.Mul
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.toLongs

class Day7 : Day {
    override val day = 7

    enum class Operation(
        val apply: (a: Long, b: Long) -> Long,
    ) {
        Add({ a, b -> a + b }),
        Mul({ a, b -> a * b }),
        Concat({ a, b -> "$a$b".toLong() }),
    }

    fun isValidCombination(
        result: Long,
        numbers: List<Long>,
        useConcat: Boolean = false,
        apply: (a: Long) -> Long = { it },
    ): Boolean {
        val resultSoFar = apply(numbers.first())
        if (numbers.size == 1) {
            return result == resultSoFar
        }

        if (resultSoFar > result) return false

        val operations = if (useConcat) Operation.entries else listOf(Add, Mul)

        return operations
            .any { op ->
                isValidCombination(
                    result,
                    numbers.drop(1),
                    useConcat,
                ) { op.apply(resultSoFar, it) }
            }
    }

    val lines by lazy {
        loadInput()
            .parseWithRegex("(\\d+):(.*)")
            .map { (resultStr, numbersStr) ->
                val result = resultStr.toLong()
                val numbers = numbersStr.trim().split(" ").toLongs()

                result to numbers
            }
    }

    override fun solvePart1() {
        lines
            .filter { (result, numbers) ->
                isValidCombination(result, numbers)
            }.sumOf { it.first }
            .solution(1)
    }

    override fun solvePart2() {
        lines
            .filter { (result, numbers) ->
                isValidCombination(result, numbers, useConcat = true)
            }.sumOf { it.first }
            .solution(2)
    }
}

fun main() = solve<Day7>()
