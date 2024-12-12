package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Cord2D
import me.reckter.aoc.cords.d2.getNeighbors
import me.reckter.aoc.cords.d2.plus
import me.reckter.aoc.days.Day12.Direction.*
import me.reckter.aoc.parseMap
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day12 : Day {
    override val day = 12

    fun Map<Cord2D<Int>, Char>.findOneRegion(start: Cord2D<Int>): Pair<Char, Set<Cord2D<Int>>> {
        val queue = ArrayDeque<Cord2D<Int>>()
        queue.add(start)
        val type = this[start] ?: error("outside of bounds")
        val ret = mutableSetOf<Cord2D<Int>>()
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

    fun Map<Cord2D<Int>, Char>.findRegions(): List<Pair<Char, Set<Cord2D<Int>>>> {
        val map = this.toMutableMap()
        val regions = mutableListOf<Pair<Char, Set<Cord2D<Int>>>>()
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
        val getPossibleNeighbours: (it: Cord2D<Int>) -> List<Cord2D<Int>>,
    ) {
        Up({ it -> listOf(it + Cord2D(-1, 0), it + Cord2D(1, 0)) }),
        Left({ it -> listOf(it + Cord2D(0, -1), it + Cord2D(0, 1)) }),
        Down({ it -> listOf(it + Cord2D(-1, 0), it + Cord2D(1, 0)) }),
        Right({ it -> listOf(it + Cord2D(0, -1), it + Cord2D(0, 1)) }),
    }

    data class Side(
        val direction: Direction,
        // sort by direction?
        val positions: Set<Cord2D<Int>>,
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

    fun Cord2D<Int>.getFreeSides(region: Set<Cord2D<Int>>): List<Side> =
        listOfNotNull(
            Side(Up, setOf(this)).takeIf { this + Cord2D(0, -1) !in region },
            Side(Left, setOf(this)).takeIf { this + Cord2D(-1, 0) !in region },
            Side(Down, setOf(this)).takeIf { this + Cord2D(0, 1) !in region },
            Side(Right, setOf(this)).takeIf { this + Cord2D(1, 0) !in region },
        )

    fun List<Side>.merge(): List<Side> {
        var current = this
        var merged = mutableListOf<Side>()
        var change = true
        while (change) {
            change = false
            current@ for (cur in current) {
                for (other in merged) {
                    if (cur.canMergeWith(other)) {
                        merged.remove(other)
                        merged.add(cur.merge(other))
                        change = true
                        continue@current
                    }
                }
                merged.add(cur)
            }
            current = merged
            merged = mutableListOf()
        }
        return current
    }

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
