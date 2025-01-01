package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.toLongs

class Day22 : Day {
    override val day = 22

    inline infix fun Long.mix(other: Long): Long = (this xor other) % 16777216L

    fun genNextSecret(current: Long): Long {
        val p1 = (current * 64) mix current
        val p2 = (p1 / 32) mix p1
        val secret = (p2 * 2048) mix p2
        return secret
    }

    fun getPriceSequences(startSecret: Long): Map<List<Long>, Long> =
        generateSequence(startSecret) {
            genNextSecret(it)
        }.take(2001)
            .map { it % 10 }
            .windowed(5, 1)
            .map {
                val changes =
                    it
                        .zipWithNext()
                        .map { (a, b) -> b - a }

                changes to it.last()
            }.fold(mutableMapOf<List<Long>, Long>()) { map, it ->
                if (!map.contains(it.first)) {
                    map[it.first] = it.second
                }
                map
            }

    override fun solvePart1() {
        loadInput()
            .toLongs()
            .map {
                generateSequence(it) {
                    genNextSecret(it)
                }.drop(2000)
                    .first()
            }.sum()
            .solution(1)
    }

    override fun solvePart2() {
        loadInput()
            .toLongs()
            .map { getPriceSequences(it) }
            .fold(mutableMapOf<List<Long>, Long>()) { map, it ->
                it.entries
                    .forEach { map[it.key] = (map[it.key] ?: 0) + it.value }

                map
            }.values
            .max()
            .solution(2)
    }
}

fun main() = solve<Day22>()
