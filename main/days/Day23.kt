package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day23 : Day {
    override val day = 23

    fun Map<String, MutableSet<String>>.findGroupsSize3(): List<List<String>> =
        this.entries
            .flatMap { (a, connectionsA) ->
                connectionsA
                    .filter { a != it }
                    .flatMap { b ->
                        val connectionsB = this[b] ?: error("$b not in map")
                        val cs =
                            connectionsA
                                .intersect(connectionsB)
                                .filter { it != a && it != b }

                        cs.map { listOf(a, b, it).sorted() }
                    }
            }.distinct()

    fun Map<String, MutableSet<String>>.findBiggestGroup(): List<String> {
        val queue = ArrayDeque(this.entries.map { setOf(it.key) to it.value.toSet() })
        val seen = mutableSetOf<Set<String>>()
        queue.onEach { seen.add(it.first) }

        while (queue.size > 0) {
            val next = queue.removeFirst()

            val contestants = next.second.minus(next.first)

            contestants
                .map {
                    (next.first + it) to
                        next.second.intersect(
                            this[it] ?: error("contestant $contestants not in map"),
                        )
                }.filter { it.first !in seen }
                .onEach {
                    seen.add(it.first)
                    queue.add(it)
                }
        }

        return seen
            .maxBy { it.size }
            .sorted()
    }

    val map by lazy {
        loadInput()
            .parseWithRegex("(..)-(..)")
            .map { (a, b) ->
                a to b
            }.fold(mutableMapOf<String, MutableSet<String>>()) { map, it ->
                map
                    .getOrPut(it.first) { mutableSetOf<String>() }
                    .apply {
                        add(it.second)
                        add(it.first)
                    }

                map
                    .getOrPut(it.second) { mutableSetOf<String>() }
                    .apply {
                        add(it.second)
                        add(it.first)
                    }

                map
            }
    }

    override fun solvePart1() {
        map
            .findGroupsSize3()
            .count {
                it.any { it.startsWith("t") }
            }.solution(1)
    }

    override fun solvePart2() {
        map
            .findBiggestGroup()
            .joinToString(",")
            .solution(2)
    }
}

fun main() = solve<Day23>()
