package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.memoize
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.splitAtEmptyLine

class Day19 : Day {
    override val day = 19

    val towels by lazy {
        loadInput(trim = false)
            .splitAtEmptyLine()
            .first()
            .first()
            .split(", ")
    }

    val patterns by lazy {
        loadInput(trim = false)
            .splitAtEmptyLine()
            .last()
    }

    lateinit var getPossibleConfigurations: (pattern: String) -> Long

    init {
        getPossibleConfigurations =
            memoize { pattern: String ->
                if (pattern == "") {
                    1L
                } else {
                    towels
                        .asSequence()
                        .filter { pattern.startsWith(it) }
                        .map { pattern.removePrefix(it) }
                        .sumOf { getPossibleConfigurations(it) }
                }
            }
    }

    override fun solvePart1() {
        patterns
            .count {
                getPossibleConfigurations(it) > 0
            }.solution(1)
    }

    override fun solvePart2() {
        patterns
            .sumOf {
                getPossibleConfigurations(it)
            }.solution(2)
    }
}

fun main() = solve<Day19>()
