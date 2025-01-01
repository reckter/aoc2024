package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.allPairings
import me.reckter.aoc.flipDimensions
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.splitAtEmptyLine

class Day25 : Day {
    override val day = 25

    val keys by lazy {
        loadInput(trim = false)
            .splitAtEmptyLine()
            .filter { it.first().all { it == '.' } }
            .map {
                it
                    .toList()
                    .map { it.toList() }
                    .flipDimensions()
                    .map { it.count { it == '#' } - 1 }
            }
    }

    val locks by lazy {
        loadInput(trim = false)
            .splitAtEmptyLine()
            .filter { it.first().all { it == '#' } }
            .map {
                it
                    .toList()
                    .map { it.toList() }
                    .flipDimensions()
                    .map { it.count { it == '#' } - 1 }
            }
    }

    override fun solvePart1() {
        keys
            .allPairings(locks)
            .count { (key, lock) ->
                key
                    .zip(lock)
                    .all { (a, b) ->
                        a + b <= 5
                    }
            }.solution(1)
    }

    override fun solvePart2() {
    }
}

fun main() = solve<Day25>()
