package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day3 : Day {
    override val day = 3

    val regex = Regex("mul\\((\\d+),(\\d+)\\)")

    override fun solvePart1() {
        val str =
            loadInput(trim = false)
                .joinToString("\n")

        regex
            .findAll(str)
            .map { match -> match.groupValues[1].toInt() * match.groupValues[2].toInt() }
            .sum()
            .solution(1)
    }

    override fun solvePart2() {
        val str =
            loadInput(trim = false)
                .joinToString("\n")
                .split("do()")
                .map { it.substringBefore("don't()") }
                .joinToString(" ")

        regex
            .findAll(str)
            .map { match -> match.groupValues[1].toInt() * match.groupValues[2].toInt() }
            .sum()
            .solution(2)
    }
}

fun main() = solve<Day3>()
