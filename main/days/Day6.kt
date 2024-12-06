package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Cord2D
import me.reckter.aoc.cords.d2.plus
import me.reckter.aoc.days.Day6.Direction.Top
import me.reckter.aoc.parseMap
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day6 : Day {
    override val day = 6

    enum class Tile {
        Free,
        Obstacle,
        StartPosition,
    }

    enum class Direction(
        val coords: Cord2D<Int>,
    ) {
        Top(Cord2D(0, -1)),
        Left(Cord2D(-1, 0)),
        Bottom(Cord2D(0, 1)),
        Right(Cord2D(1, 0)),
        ;

        fun turnRight(): Direction =
            when (this) {
                Top -> Right
                Left -> Top
                Bottom -> Left
                Right -> Bottom
            }
    }

    fun Map<Cord2D<Int>, Tile>.walkStep(
        position: Cord2D<Int>,
        direction: Direction,
    ): Pair<Cord2D<Int>, Direction>? {
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

    fun Map<Cord2D<Int>, Tile>.hasLoop(
        position: Cord2D<Int>,
        direction: Direction,
    ): Boolean {
        val seen = mutableSetOf<Pair<Cord2D<Int>, Direction>>()
        var current: Pair<Cord2D<Int>, Direction>? = position to direction
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
            .filter { it != startPosition }
            .count {
                val newMap = (map + (it to Tile.Obstacle))
                newMap.hasLoop(startPosition, Top)
            }.solution(2)
    }
}

fun main() = solve<Day6>()
