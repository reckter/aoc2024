package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.binarySearch
import me.reckter.aoc.cords.d2.Coord2D
import me.reckter.aoc.cords.d2.getNeighbors
import me.reckter.aoc.dijkstraInt
import me.reckter.aoc.dijkstraIntOrNull
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.solution
import me.reckter.aoc.timed

class Day18 : Day {
    override val day = 18

    val fallingBytes by lazy {
        loadInput()
            .parseWithRegex("(\\d+),(\\d+)")
            .map { (xStr, yStr) ->
                Coord2D(xStr.toInt(), yStr.toInt())
            }
    }

    override fun solvePart1() {
        val brokenBytes =
            fallingBytes
                .take(1024)
                .toSet()

        dijkstraInt(
            start = Coord2D(0, 0),
            end = Coord2D(70, 70),
            getNeighbors = { it ->
                it
                    .last()
                    .getNeighbors(noEdges = true)
                    .filter { it !in brokenBytes }
                    .filter { it.x in (0..70) }
                    .filter { it.y in (0..70) }
            },
            getWeightBetweenNodes = { _, _ -> 1 },
        ).second
            .solution(1)
    }

    override fun solvePart2() {
        val blockingStoneIndex =
            binarySearch(0, fallingBytes.size - 1L) {
                val set =
                    fallingBytes
                        .take(it.toInt())
                        .toSet()

                dijkstraIntOrNull(
                    start = Coord2D(0, 0),
                    end = Coord2D(70, 70),
                    getNeighbors = {
                        it
                            .last()
                            .getNeighbors(noEdges = true)
                            .filter { it !in set }
                            .filter { it.x in (0..70) }
                            .filter { it.y in (0..70) }
                    },
                    getWeightBetweenNodes = { _, _ -> 1 },
                ) == null
            }

        fallingBytes[blockingStoneIndex.toInt() - 1]
            .let { "${it.x},${it.y}" }
            .solution(2)
    }
}

fun main() = timed<Day18>(Day18::class.java)
