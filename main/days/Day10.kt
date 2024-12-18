package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Coord2D
import me.reckter.aoc.cords.d2.getNeighbors
import me.reckter.aoc.parseMap
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day10 : Day {
    override val day = 10

    fun Map<Coord2D<Int>, Int>.findTrails(current: Coord2D<Int>): List<List<Coord2D<Int>>> {
        val height = this[current] ?: return emptyList()
        if (height == 9) return listOf(listOf(current))

        return current
            .getNeighbors(noEdges = true)
            .filter { (this[it] ?: -1) == height + 1 }
            .flatMap { this.findTrails(it).map { list -> listOf(it) + list } }
            .distinct()
    }

    val map by lazy {
        loadInput()
            .parseMap { it.toString().toInt() }
    }

    override fun solvePart1() {
        map.entries
            .filter { it.value == 0 }
            .sumOf {
                map
                    .findTrails(it.key)
                    .map { it.last() }
                    .distinct()
                    .size
            }.solution(1)
    }

    override fun solvePart2() {
        map.entries
            .filter { it.value == 0 }
            .sumOf {
                map
                    .findTrails(it.key)
                    .size
            }.solution(2)
    }
}

fun main() = solve<Day10>()
