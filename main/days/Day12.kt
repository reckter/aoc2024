package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Coord2D
import me.reckter.aoc.cords.d2.getNeighbors
import me.reckter.aoc.cords.d2.plus
import me.reckter.aoc.days.Day12.Direction.*
import me.reckter.aoc.parseMap
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day12 : Day {
    override val day = 12

    fun Map<Coord2D<Int>, Char>.findOneRegion(start: Coord2D<Int>): Pair<Char, Set<Coord2D<Int>>> {
        val queue = ArrayDeque<Coord2D<Int>>()
        queue.add(start)
        val type = this[start] ?: error("outside of bounds")
        val ret = mutableSetOf<Coord2D<Int>>()
        while (queue.isNotEmpty()) {
            val next = queue.removeFirst()
            if (ret.contains(next)) continue

            ret.add(next)
            next
                .getNeighbors(noEdges = true)
                .filter { it !in ret }
                .filter { it !in queue }
                .filter { this[it] == type }
                .forEach { queue.add(it) }
        }
        return type to ret
    }

    fun Map<Coord2D<Int>, Char>.findRegions(): List<Pair<Char, Set<Coord2D<Int>>>> {
        val map = this.toMutableMap()
        val regions = mutableListOf<Pair<Char, Set<Coord2D<Int>>>>()
        while (map.isNotEmpty()) {
            val start = map.keys.first()
            val region = map.findOneRegion(start)
            regions.add(region)
            region.second.forEach { map.remove(it) }
        }
        return regions
    }

    val map by lazy {
        loadInput()
            .parseMap { it }
    }

    override fun solvePart1() {
        map
            .findRegions()
            .sumOf { region ->
                val area = region.second.size
                val boundary =
                    region.second
                        .sumOf {
                            it
                                .getNeighbors(noEdges = true)
                                .count { !region.second.contains(it) }
                        }

                area * boundary
            }.solution(1)
    }

    enum class Direction(
        val getPossibleNeighbours: (it: Coord2D<Int>) -> List<Coord2D<Int>>,
    ) {
        Up({ it -> listOf(it + Coord2D(-1, 0), it + Coord2D(1, 0)) }),
        Left({ it -> listOf(it + Coord2D(0, -1), it + Coord2D(0, 1)) }),
        Down({ it -> listOf(it + Coord2D(-1, 0), it + Coord2D(1, 0)) }),
        Right({ it -> listOf(it + Coord2D(0, -1), it + Coord2D(0, 1)) }),
    }

    data class Side(
        val direction: Direction,
        val positions: Set<Coord2D<Int>>,
    ) {
        fun canMergeWith(other: Side): Boolean {
            if (direction != other.direction) return false

            return positions.any {
                direction
                    .getPossibleNeighbours(it)
                    .any {
                        it in other.positions
                    }
            }
        }

        fun merge(with: Side): Side =
            this.copy(
                positions = positions + with.positions,
            )
    }

    fun Coord2D<Int>.getFreeSides(region: Set<Coord2D<Int>>): List<Side> =
        listOfNotNull(
            Side(Up, setOf(this)).takeIf { this + Coord2D(0, -1) !in region },
            Side(Left, setOf(this)).takeIf { this + Coord2D(-1, 0) !in region },
            Side(Down, setOf(this)).takeIf { this + Coord2D(0, 1) !in region },
            Side(Right, setOf(this)).takeIf { this + Coord2D(1, 0) !in region },
        )

    fun List<Side>.merge(): List<Side> =
        generateSequence(this) { current ->
            current.fold(emptyList()) { cur, side ->
                val candidate = cur.find { it.canMergeWith(side) }
                if (candidate == null) {
                    cur + side
                } else {
                    (cur - candidate) + candidate.merge(side)
                }
            }
        }.zipWithNext()
            .dropWhile { it.first.size != it.second.size }
            .first()
            .second

    override fun solvePart2() {
        map
            .findRegions()
            .sumOf { region ->
                val area = region.second.size
                val sides =
                    region.second
                        .flatMap {
                            it.getFreeSides(region.second)
                        }.merge()
                area * sides.size
            }.solution(2)
    }
}

fun main() = solve<Day12>()
