package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Coord2D
import me.reckter.aoc.cords.d2.plus
import me.reckter.aoc.days.Day15.Tile.Box
import me.reckter.aoc.days.Day15.Tile.BoxLeft
import me.reckter.aoc.days.Day15.Tile.BoxRight
import me.reckter.aoc.days.Day15.Tile.Free
import me.reckter.aoc.days.Day15.Tile.Robot
import me.reckter.aoc.days.Day15.Tile.Wall
import me.reckter.aoc.parseMap
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.splitAtEmptyLine

class Day15 : Day {
    override val day = 15

    enum class Tile {
        Free,
        Box,
        BoxLeft,
        BoxRight,
        Wall,
        Robot,
    }

    enum class Direction(
        val vector: Coord2D<Int>,
    ) {
        Up(Coord2D(0, -1)),
        Right(Coord2D(1, 0)),
        Down(Coord2D(0, 1)),
        Left(Coord2D(-1, 0)),
    }

    val startMap by lazy {
        loadInput(trim = false)
            .splitAtEmptyLine()
            .first()
            .toList()
            .parseMap {
                when (it) {
                    '.' -> Tile.Free
                    'O' -> Tile.Box
                    '#' -> Tile.Wall
                    '@' -> Tile.Robot
                    else -> error("no definition for '$it'")
                }
            }
    }

    fun Map<Coord2D<Int>, Tile>.print(robot: Coord2D<Int>) {
        val minX = this.keys.minOf { it.x }
        val maxX = this.keys.maxOf { it.x }
        val minY = this.keys.minOf { it.y }
        val maxY = this.keys.maxOf { it.y }

        (minY..maxY).forEach { y ->
            (minX..maxX).forEach { x ->

                val tile = if (robot.x == x && robot.y == y) Tile.Robot else this[Coord2D(x, y)] ?: Tile.Free
                print(
                    when (tile) {
                        Free -> "."
                        Box -> "O"
                        BoxLeft -> "["
                        BoxRight -> "]"
                        Wall -> "#"
                        Robot -> "@"
                    },
                )
            }
            println()
        }
    }

    val instructions by lazy {
        loadInput(trim = false)
            .splitAtEmptyLine()
            .last()
            .joinToString("")
            .map {
                when (it) {
                    '^' -> Direction.Up
                    '>' -> Direction.Right
                    'v' -> Direction.Down
                    '<' -> Direction.Left
                    else -> error("Invalid direction: '$it'")
                }
            }
    }

    fun tryMove(
        map: MutableMap<Coord2D<Int>, Tile>,
        robot: Coord2D<Int>,
        direction: Direction,
    ): Coord2D<Int> {
        val next = robot + direction.vector

        val queue = ArrayDeque<Coord2D<Int>>()
        val seen = mutableSetOf<Coord2D<Int>>()
        queue.add(next)

        while (queue.size > 0) {
            val n = queue.removeFirst()

            if (n in seen) continue

            // we hit a wall, abort
            if (map[n] == Wall) return robot

            if (map[n] !in listOf(Box, BoxRight, BoxLeft)) {
                // we hit empty space, nothing to do
                continue
            }

            seen.add(n)

            if (map[n] == BoxLeft) {
                queue.add(n + direction.vector)
                queue.add(n + Coord2D(1, 0))
            } else if (map[n] == BoxRight) {
                queue.add(n + direction.vector)
                queue.add(n + Coord2D(-1, 0))
            } else {
                queue.add(n + direction.vector)
            }
        }

        val newBoxes = seen.map { it + direction.vector to (map[it] ?: error(" No box? at $it")) }
        seen.forEach { map.remove(it) }
        newBoxes.forEach { map.put(it.first, it.second) }

        return next
    }

    override fun solvePart1() {
        val robot =
            startMap
                .filter { it.value == Robot }
                .entries
                .single()
                .key

        val mapWithoutRobot = (startMap - robot).toMutableMap()

        instructions
            .fold(robot) { current, instr ->
                tryMove(mapWithoutRobot, current, instr)
            }

        mapWithoutRobot
            .filter { it.value == Box }
            .keys
            .sumOf { it.x + it.y * 100 }
            .solution(1)
    }

    override fun solvePart2() {
        val dublicateDirection = Coord2D(1, 0)
        val map =
            startMap
                .entries
                .flatMap {
                    val pos = Coord2D(it.key.x + it.key.x, it.key.y)
                    when (it.value) {
                        Free -> listOf(pos to it.value, (pos + dublicateDirection) to it.value)
                        Box -> listOf(pos to BoxLeft, (pos + dublicateDirection) to BoxRight)
                        BoxLeft -> TODO()
                        BoxRight -> TODO()
                        Wall -> listOf(pos to it.value, pos + dublicateDirection to it.value)
                        Robot -> listOf(pos to it.value, pos + dublicateDirection to Free)
                    }
                }.toMap()

        val robot =
            map
                .filter { it.value == Robot }
                .entries
                .single()
                .key

        val mapWithoutRobot = (map - robot).toMutableMap()

        instructions
            .fold(robot) { current, instr ->
                val res = tryMove(mapWithoutRobot, current, instr)

                res
            }

        mapWithoutRobot
            .filter { it.value == BoxLeft }
            .keys
            .sumOf { it.x + it.y * 100 }
            .solution(2)
    }
}

fun main() = solve<Day15>()
