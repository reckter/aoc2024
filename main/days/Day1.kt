package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import kotlin.math.abs

class Day1 : Day {
    override val day = 1

    val lists by lazy {
        loadInput()
            .parseWithRegex("(\\d+) *(\\d+)")
            .map { (firstStr, secondStr) -> firstStr.toInt() to secondStr.toInt() }
            .unzip()
            .toList()
            .map { it.sorted() }
    }

    override fun solvePart1() {
        lists
            .let {
                it.first().zip(it.last())
            }.sumOf { (first, second) -> abs(first - second) }
            .solution(1)
    }

    override fun solvePart2() {
        val (first, second) = lists
        first
            .sumOf { num ->
                num * second.count { num == it }
            }.solution(2)
    }
}

fun main() = solve<Day1>()
