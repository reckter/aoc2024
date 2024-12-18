package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.allPairings
import me.reckter.aoc.cords.d2.Coord2D
import me.reckter.aoc.cords.d2.minus
import me.reckter.aoc.cords.d2.plus
import me.reckter.aoc.gcd
import me.reckter.aoc.parseMap
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day8 : Day {
    override val day = 8

    val map by lazy {
        loadInput()
            .parseMap { it }
    }

    override fun solvePart1() {
        map.entries
            .filter { it.value != '.' }
            .groupBy { it.value }
            .mapValues { it.value.map { it.key } }
            .entries
            .flatMap {
                it.value
                    .allPairings(bothDirections = true)
                    .map { (first, second) ->
                        val distance = first - second
                        first + distance
                    }
            }.distinct()
            .filter { map.contains(it) }
            .count()
            .solution(1)
    }

    override fun solvePart2() {
        map.entries
            .filter { it.value != '.' }
            .groupBy { it.value }
            .mapValues { it.value.map { it.key } }
            .entries
            .flatMap {
                it.value
                    .allPairings()
                    .flatMap { (first, second) ->
                        val distance = first - second
                        val factor = gcd(distance.x.toLong(), distance.y.toLong()).toInt()
                        val direction = Coord2D(distance.x / factor, distance.y / factor)
                        val oneDirection =
                            generateSequence(first) { it + direction }
                                .takeWhile { map.contains(it) }
                        val otherDirection =
                            generateSequence(first - direction) { it - direction }
                                .takeWhile { map.contains(it) }
                        oneDirection + otherDirection
                    }
            }.distinct()
            .filter { map.contains(it) }
            .count()
            .solution(2)
    }
}

fun main() = solve<Day8>()
