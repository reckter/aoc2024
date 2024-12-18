package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Coord2D
import me.reckter.aoc.cords.d2.plus
import me.reckter.aoc.days.Day6.Direction.Top
import me.reckter.aoc.parseMap
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import kotlin.streams.asStream

class Day6 : Day {
    override val day = 6

    enum class Tile {
        Free,
        Obstacle,
        StartPosition,
    }

    enum class Direction(
        val coords: Coord2D<Int>,
    ) {
        Top(Coord2D(0, -1)),
        Left(Coord2D(-1, 0)),
        Bottom(Coord2D(0, 1)),
        Right(Coord2D(1, 0)),
        ;

        fun turnRight(): Direction =
            when (this) {
                Top -> Right
                Left -> Top
                Bottom -> Left
                Right -> Bottom
            }
    }

    fun Map<Coord2D<Int>, Tile>.walkStep(
        position: Coord2D<Int>,
        direction: Direction,
    ): Pair<Coord2D<Int>, Direction>? {
        val nextTile = this[position + direction.coords] ?: return null

        if (nextTile == Tile.Obstacle) {
            return this.walkStep(position, direction.turnRight())
        }

        return position + direction.coords to direction
    }

    val map by lazy {
        loadInput()
            .parseMap {
                when (it) {
                    '#' -> Tile.Obstacle
                    '.' -> Tile.Free
                    '^' -> Tile.StartPosition
                    else -> error("no tile for `$it`")
                }
            }
    }

    val startPosition by lazy {
        map
            .filterValues { it == Tile.StartPosition }
            .keys
            .single()
    }

    override fun solvePart1() {
        generateSequence(startPosition to Direction.Top) { (pos, direction) ->
            map.walkStep(pos, direction)
        }.map { it.first }
            .distinct()
            .count()
            .solution(1)
    }

    fun Map<Coord2D<Int>, Tile>.hasLoop(
        position: Coord2D<Int>,
        direction: Direction,
    ): Boolean {
        val seen = mutableSetOf<Pair<Coord2D<Int>, Direction>>()
        var current: Pair<Coord2D<Int>, Direction>? = position to direction
        while (current != null) {
            seen.add(current)
            current = this.walkStep(current.first, current.second)
            if (current in seen) return true
        }
        return false
    }

    override fun solvePart2() {
        generateSequence(startPosition to Direction.Top) { (pos, direction) ->
            map.walkStep(pos, direction)
        }.map { it.first }
            .distinct()
            .asStream()
            .parallel()
            .filter { it != startPosition }
            .filter {
                val newMap = (map + (it to Tile.Obstacle))
                newMap.hasLoop(startPosition, Top)
            }.count()
            .solution(2)
    }
}

fun main() = solve<Day6>()
