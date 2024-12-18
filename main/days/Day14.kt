package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Coord2D
import me.reckter.aoc.cords.d2.getNeighbors
import me.reckter.aoc.cords.d2.plus
import me.reckter.aoc.lcm
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import java.util.ArrayDeque

class Day14 : Day {
    override val day = 14

    data class Robot(
        val position: Coord2D<Int>,
        val velocity: Coord2D<Int>,
    ) {
        fun move(bounds: Coord2D<Int>): Robot = this.copy(position = (this.position + this.velocity) % bounds)

        fun moveXSteps(
            bounds: Coord2D<Int>,
            steps: Int,
        ): Robot = this.copy(position = ((this.position + this.velocity * steps) % bounds))
    }

    fun List<Robot>.countInBounds(
        start: Coord2D<Int>,
        end: Coord2D<Int>,
    ): Int =
        this.count {
            it.position.x > start.x &&
                it.position.x < end.x &&
                it.position.y > start.y &&
                it.position.y < end.y
        }

    fun List<Robot>.printMap(bounds: Coord2D<Int>) {
        (0 until bounds.y).forEach { y ->
            (0 until bounds.x).forEach { x ->
                val count = this.count { it.position.x == x && it.position.y == y }
                if (count == 0) {
                    print(".")
                } else {
                    print("#")
                }
            }
            println(".")
        }
    }

    val start by lazy {
        loadInput()
            .parseWithRegex("p=(\\d+),(\\d+) v=(-?\\d+),(-?\\d+)")
            .map { (xStr, yStr, xVelStr, yVelStr) ->
                Robot(
                    Coord2D(xStr.toInt(), yStr.toInt()),
                    Coord2D(xVelStr.toInt(), yVelStr.toInt()),
                )
            }
    }

    override fun solvePart1() {
        val bounds = Coord2D(101, 103)
        start
            .let {
                (1..100).fold(it) { acc, i ->
                    acc.map { it.move(bounds) }
                }
            }.let { robots ->
                listOf(
                    Coord2D(-1, -1) to Coord2D(bounds.x / 2, bounds.y / 2),
                    Coord2D(bounds.x / 2, -1) to Coord2D(bounds.x, bounds.y / 2),
                    Coord2D(-1, bounds.y / 2) to Coord2D(bounds.x / 2, bounds.y),
                    Coord2D(bounds.x / 2, bounds.y / 2) to bounds,
                ).map {
                    robots.countInBounds(it.first, it.second).toLong()
                }.reduce { acc, it -> acc * it }
            }.solution(1)
    }

    fun List<Robot>.areInOneShape(): Boolean {
        val shapes = mutableListOf<Set<Coord2D<Int>>>()
        val map =
            this
                .groupBy { it.position }
                .toMutableMap()
        while (map.isNotEmpty()) {
            val queue = ArrayDeque<Coord2D<Int>>()
            val seen = mutableSetOf<Coord2D<Int>>()
            queue.add(map.keys.first())
            while (queue.size > 0) {
                val next = queue.removeFirst()
                seen.add(next)
                next
                    .getNeighbors()
                    .filter { it !in seen }
                    .filter { it in map }
                    .filter { it !in queue }
                    .forEach { queue.add(it) }
            }
            shapes.add(seen)
            if (shapes.size > 150) return false
            seen.forEach { map.remove(it) }
        }

        return shapes.size < 200
    }

    fun findCycle(
        start: Int,
        velocity: Int,
        bound: Int,
    ): Long =
        generateSequence(1L) { it + 1L }
            .dropWhile { (it * velocity) % bound.toLong() != 0L }
            .first()

    fun Robot.findCycle(bounds: Coord2D<Int>): Long {
        val x = findCycle(this.position.x, this.velocity.x, bounds.x)
        val y = findCycle(this.position.y, this.velocity.y, bounds.y)

        return lcm(x, y)
    }

    override fun solvePart2() {
        val bounds = Coord2D(101, 103)

        val step =
            generateSequence(1) {
                it + 1
            }.dropWhile { steps ->
                val map = start.map { it.moveXSteps(bounds, steps) }

                !map.areInOneShape()
            }.first()

// 		start.map { it.moveXSteps(bounds, step) }
// 			.printMap(bounds)

        step
            .solution(2)
    }
}

private operator fun Coord2D<Int>.times(steps: Int): Coord2D<Int> = Coord2D(this.x * steps, this.y * steps)

private operator fun Coord2D<Int>.rem(bounds: Coord2D<Int>): Coord2D<Int> {
    var y = (this.y) % bounds.y
    var x = (this.x) % bounds.x
    if (x < 0) x = x + bounds.x
    if (y < 0) y = y + bounds.y
    return Coord2D(x, y)
}

fun main() = solve<Day14>()
