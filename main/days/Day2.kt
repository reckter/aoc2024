package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.leaveOutOne
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.toIntegers
import kotlin.math.absoluteValue

class Day2 : Day {
    override val day = 2

    override fun solvePart1() {
        loadInput()
            .map {
                it.split(" ").toIntegers()
            }.filter { it.zipWithNext().all { (a, b) -> (b - a).absoluteValue in listOf(1, 2, 3) } }
            .filter { it.zipWithNext().all { (a, b) -> b - a > 0 } || it.zipWithNext().all { (a, b) -> b - a < 0 } }
            .count()
            .solution(1)
    }

    override fun solvePart2() {
        loadInput()
            .map {
                it.split(" ").toIntegers()
            }.filter {
                it
                    .leaveOutOne()
                    .any {
                        it.zipWithNext().all { (a, b) -> (b - a).absoluteValue in listOf(1, 2, 3) } &&
                            (
                                it.zipWithNext().all { (a, b) -> b - a > 0 } ||
                                    it
                                        .zipWithNext()
                                        .all { (a, b) -> b - a < 0 }
                            )
                    }
            }.count()
            .solution(1)
    }
}

fun main() = solve<Day2>()
