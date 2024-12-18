package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Coord2D
import me.reckter.aoc.cords.d2.plus
import me.reckter.aoc.parseMap
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day4 : Day {
    override val day = 4

    fun Map<Coord2D<Int>, Char>.checkWordAtPosInDirection(
        start: Coord2D<Int>,
        direction: Coord2D<Int>,
        word: String,
    ): Boolean {
        var currentPos = start
        for (char in word) {
            if (this[currentPos] != char) {
                return false
            }
            currentPos += direction
        }
        return true
    }

    fun Map<Coord2D<Int>, Char>.checkWordAtPos(
        start: Coord2D<Int>,
        word: String,
    ): Int =
        listOf(
            Coord2D(1, 0),
            Coord2D(0, 1),
            Coord2D(-1, 0),
            Coord2D(0, -1),
            Coord2D(1, 1),
            Coord2D(1, -1),
            Coord2D(-1, 1),
            Coord2D(-1, -1),
        ).count { checkWordAtPosInDirection(start, it, word) }

    fun Map<Coord2D<Int>, Char>.checkForPattern(
        start: Coord2D<Int>,
        pattern: List<String>,
    ): Boolean {
        pattern.mapIndexed { y, row ->
            row.mapIndexed { x, char ->
                if (char != ' ' && this.getOrDefault(start + Coord2D(x, y), null) != char) {
                    return false
                }
            }
        }
        return true
    }

    fun Map<Coord2D<Int>, Char>.checkForCross(start: Coord2D<Int>): Int {
        val patterns =
            listOf(
                """
                M M
                 A 
                S S
                """.trimIndent(),
                """
                M S
                 A 
                M S
                """.trimIndent(),
                """
                S S
                 A 
                M M
                """.trimIndent(),
                """
                S M
                 A 
                S M
                """.trimIndent(),
            ).map { it.split("\n") }
        return patterns.count { checkForPattern(start, it) }
    }

    override fun solvePart1() {
        val map =
            loadInput()
                .parseMap { it }

        val minX = map.keys.minOf { it.x }
        val maxX = map.keys.maxOf { it.x }
        val minY = map.keys.minOf { it.y }
        val maxY = map.keys.maxOf { it.y }

        val wordToFind = "XMAS"

        (minX..maxX)
            .sumOf { x ->
                (minY..maxY)
                    .sumOf { y ->
                        map.checkWordAtPos(Coord2D(x, y), wordToFind)
                    }
            }.solution(1)
    }

    override fun solvePart2() {
        val map =
            loadInput()
                .parseMap { it }

        val minX = map.keys.minOf { it.x }
        val maxX = map.keys.maxOf { it.x }
        val minY = map.keys.minOf { it.y }
        val maxY = map.keys.maxOf { it.y }

        val wordToFind = "XMAS"

        (minX..maxX)
            .sumOf { x ->
                (minY..maxY)
                    .sumOf { y ->
                        map.checkForCross(Coord2D(x, y))
                    }
            }.solution(2)
    }
}

fun main() = solve<Day4>()
