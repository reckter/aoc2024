package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Coord2D
import me.reckter.aoc.cords.d2.getNeighbors
import me.reckter.aoc.cords.d2.manhattenDistance
import me.reckter.aoc.cords.d2.minus
import me.reckter.aoc.cords.d2.plus
import me.reckter.aoc.days.Day20.Tile.End
import me.reckter.aoc.days.Day20.Tile.Start
import me.reckter.aoc.days.Day20.Tile.Wall
import me.reckter.aoc.dijkstraForAllPoints
import me.reckter.aoc.dijkstraInt
import me.reckter.aoc.parseMap
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import java.util.*

class Day20 : Day {
    override val day = 20

    enum class Tile {
        Empty,
        Wall,
        Start,
        End,
    }

    val map by lazy {
        loadInput()
            .parseMap {
                when (it) {
                    '.' -> Tile.Empty
                    '#' -> Tile.Wall
                    'S' -> Tile.Start
                    'E' -> Tile.End
                    else -> error("Invalid tile $it")
                }
            }
    }

    val start by lazy {
        map.entries
            .single { it.value == Start }
            .key
    }

    val end by lazy {
        map.entries
            .single { it.value == End }
            .key
    }

    val costFromStart by lazy {
        dijkstraForAllPoints(
            start,
            { it -> it.last().getNeighbors(noEdges = true).filter { map[it] != Wall } },
            { _, _ -> 1 },
        )
    }

    val costFromEnd by lazy {
        dijkstraForAllPoints(
            end,
            { it -> it.last().getNeighbors(noEdges = true).filter { map[it] != Wall } },
            { _, _ -> 1 },
        )
    }

    val withoutCheat by lazy {
        dijkstraInt(
            start,
            end,
            { path ->
                path
                    .last()
                    .getNeighbors(noEdges = true)
                    .filter { map[it] != Wall }
            },
            { _, _ -> 1 },
        ).second
    }

    fun findShorterCheatPathCountWithCheatHop(hop: Int): Int =
        map
            .entries
            .asSequence()
            .filter { it.value != Wall }
            .flatMap { (start) ->
                (0..hop)
                    .flatMap { xDiff ->
                        (0..(hop - xDiff)).flatMap { yDiff ->
                            listOf(
                                start + Coord2D(-xDiff, -yDiff),
                                start + Coord2D(xDiff, -yDiff),
                                start + Coord2D(-xDiff, yDiff),
                                start + Coord2D(xDiff, yDiff),
                            )
                        }
                    }.distinct()
                    .filter { it != start }
                    .filter { it in map && map[it] != Wall }
                    .map { start to it }
            }.map {
                (costFromStart[it.first] ?: error("no cost from start for ${it.first}")) +
                    it.second.manhattenDistance(
                        it.first,
                    ) +
                    (costFromEnd[it.second] ?: error("no costFrom end for ${it.second}"))
            }.filter { it <= withoutCheat - 100 }
            .count()

    override fun solvePart1() {
        findShorterCheatPathCountWithCheatHop(2)
            .solution(1)
    }

    override fun solvePart2() {
        findShorterCheatPathCountWithCheatHop(20)
            .solution(2)
    }
}

fun main() = solve<Day20>()
