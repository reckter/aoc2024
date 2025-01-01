package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.allPairings
import me.reckter.aoc.cords.d2.Coord2D
import me.reckter.aoc.cords.d2.getNeighbors
import me.reckter.aoc.dijkstraWithAllBestPaths
import me.reckter.aoc.memoize
import me.reckter.aoc.repeatToSequence
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.splitAt

class Day21 : Day {
    override val day = 21

    val keypad =
        mapOf(
            '7' to Coord2D(0, 0),
            '8' to Coord2D(1, 0),
            '9' to Coord2D(2, 0),
            '4' to Coord2D(0, 1),
            '5' to Coord2D(1, 1),
            '6' to Coord2D(2, 1),
            '1' to Coord2D(0, 2),
            '2' to Coord2D(1, 2),
            '3' to Coord2D(2, 2),
            '0' to Coord2D(1, 3),
            'A' to Coord2D(2, 3),
        )

    val directionPad =
        mapOf(
            '^' to Coord2D(1, 0),
            'A' to Coord2D(2, 0),
            '<' to Coord2D(0, 1),
            'v' to Coord2D(1, 1),
            '>' to Coord2D(2, 1),
        )
    val maps =
        listOf(
            keypad,
            directionPad,
            directionPad,
        )

    val invertedMaps =
        maps
            .distinct()
            .associateWith { it.entries.map { it.value to it.key }.toMap() }

    lateinit var costForMovement: (maps: List<Map<Char, Coord2D<Int>>>, code: List<Char>) -> Long

    init {
        costForMovement =
            memoize { maps, code ->
                val map = maps.first()
                val inverted = invertedMaps[map] ?: error("no inverted map!")

                val allPaths =
                    (listOf('A') + code)
                        .zipWithNext()
                        .map { (from, to) ->

                            val start = map[from] ?: error("no pos for $from")
                            val end = map[to] ?: error("no pos for $to")

                            dijkstraWithAllBestPaths(
                                start,
                                { node -> node == end },
                                {
                                    it
                                        .last()
                                        .getNeighbors(noEdges = true)
                                        .filter { it in inverted }
                                },
                                { _, _ -> 1 },
                            )
                        }.map { paths ->
                            paths.map { path ->
                                path.first
                                    .zipWithNext()
                                    .flatMap { (a, b) ->
                                        val xDirection =
                                            (b.x - a.x)
                                                .let {
                                                    when {
                                                        it == 0 -> emptyList()
                                                        it > 0 -> listOf('>')
                                                        else -> listOf('<')
                                                    }
                                                }
                                        val yDirection =
                                            (b.y - a.y)
                                                .let {
                                                    when {
                                                        it == 0 -> emptyList()
                                                        it > 0 -> listOf('v')
                                                        else -> listOf('^')
                                                    }
                                                }

                                        xDirection.toList() + yDirection.toList()
                                    } + 'A'
                            }
                        }.fold(listOf<List<Char>>(emptyList())) { lists, curr ->
                            lists
                                .allPairings(curr)
                                .map { (a, b) -> a + b }
                                .toList()
                        }.distinct()

                val mapRest = maps.drop(1)

                if (mapRest.isEmpty()) {
                    allPaths
                        .minOf { it.size.toLong() }
                } else {
                    allPaths
                        .map { path ->

                            val splits = path.splitAt { it == 'A' }.toList()
                            path to
                                splits
                                    .mapIndexed { i, it ->
                                        costForMovement(mapRest, it.toList() + if (i < splits.size - 1) listOf('A') else emptyList())
                                    }.sum()
                        }.minOf { it.second }
                }
            }
    }

    override fun solvePart1() {
        loadInput()
            .map { code ->
                code to costForMovement(maps, code.toList())
            }.onEach { println(it) }
            .sumOf { it.first.dropLast(1).toInt() * it.second }
            .solution(1)
    }

    override fun solvePart2() {
        val maps =
            listOf(
                keypad,
            ) + directionPad.repeatToSequence(25).toList()

        loadInput()
            .map { code ->
                code to costForMovement(maps, code.toList())
            }.sumOf { it.first.dropLast(1).toLong() * it.second }
            .solution(2)
    }
}

fun main() = solve<Day21>()
