package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Cord2D
import me.reckter.aoc.cords.d2.plus
import me.reckter.aoc.days.Day16.Tile.End
import me.reckter.aoc.days.Day16.Tile.Start
import me.reckter.aoc.dijkstraInt
import me.reckter.aoc.memoize
import me.reckter.aoc.parseMap
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import java.util.*
import kotlin.Comparator

class Day16 : Day {
	override val day = 16

	enum class Tile {
		Empty,
		Wall,
		Start,
		End
	}

	enum class Direction(val vector: Cord2D<Int>) {
		North(Cord2D(0, -1)),
		East(Cord2D(1, 0)),
		South(Cord2D(0, 1)),
		West(Cord2D(-1, 0));

		fun turnDirections(): List<Direction> {
			return when (this) {
				North -> listOf(East, West)
				East -> listOf(North, South)
				South -> listOf(East, West)
				West -> listOf(North, South)
			}
		}
	}

	val map by lazy {
		loadInput()
			.parseMap {
				when (it) {
					'.' -> Tile.Empty
					'#' -> Tile.Wall
					'S' -> Tile.Start
					'E' -> Tile.End
					else -> error("Invalide tile '$it'")
				}
			}
	}

	fun getFastestPath(): Pair<List<Pair<Cord2D<Int>, Direction>>, Int> {
		val start = map.entries.single { it.value == Start }.key
		return dijkstraInt(
			start = start to Direction.East,
			isEnd = { it -> map[it.first] == End },
			getNeighbors = { it ->
				val last = it.last()
				val previous = it.dropLast(1).lastOrNull()
				val turns = if (previous == null || previous.second == last.second) {
					last.second.turnDirections().map { last.first to it }
				} else emptyList<Pair<Cord2D<Int>, Direction>>()

				val posForward = last.first + last.second.vector

				val forwardNeighbour = if (map[posForward] != Tile.Wall) {
					listOf(posForward to last.second)
				} else emptyList<Pair<Cord2D<Int>, Direction>>()

				turns + forwardNeighbour
			},
			getWeightBetweenNodes = { a, b ->
				if (a.second != b.second) 1000
				else 1
			}
		)
	}

	override fun solvePart1() {
		val path = getFastestPath()
		path.second
			.solution(1)
	}

	fun <Node> dijkstraWithAllBestPaths(
		start: Node,
		isEnd: (Node) -> Boolean,
		getNeighbors: (history: List<Node>) -> List<Node>,
		getWeightBetweenNodes: (from: Node, to: Node) -> Int,
		max: Int
	): List<Pair<List<Node>, Int>> {
		val queue = PriorityQueue<Pair<List<Node>, Int>>(Comparator.comparing { it.second })

		val seen = mutableMapOf<Node, Pair<Int, MutableList<Node>>>(start to (0 to mutableListOf<Node>()))
		queue.add(listOf(start) to 0)

		val result = mutableListOf<Pair<List<Node>, Int>>()
		while (queue.isNotEmpty()) {
			val next = queue.remove()
			if (isEnd(next.first.last())) {
				val end = next.first.last()
				val existingSeen = seen[end] ?: (next.second to mutableListOf())

				existingSeen.second.add(next.first.last())

				if (result.isNotEmpty() && result[0].second != next.second) {
					continue
				}

				result.add(next)
			}
			getNeighbors(next.first)
				.filter {
					val cost = next.second + getWeightBetweenNodes(next.first.last(), it)
					(it !in seen || (seen[it]?.first ?: 0) == cost) && cost <= max
				}
				.forEach {
					val cost = next.second + getWeightBetweenNodes(next.first.last(), it)
					val existingSeen = seen[it] ?: (cost to mutableListOf())

					existingSeen.second.add(next.first.last())
					seen[it] = existingSeen
					queue.add(
						(next.first + it) to cost
					)
				}
		}
		return result
	}

	override fun solvePart2() {
		val path = getFastestPath()
		val start = map.entries.single { it.value == Start }.key
		val paths = dijkstraWithAllBestPaths(
			start = start to Direction.East,
			isEnd = { it -> map[it.first] == End },
			getNeighbors = { it ->
				val last = it.last()
				val previous = it.dropLast(1).lastOrNull()
				val turns = if (previous == null || previous.second == last.second) {
					last.second.turnDirections().map { last.first to it }
				} else emptyList<Pair<Cord2D<Int>, Direction>>()

				val posForward = last.first + last.second.vector

				val forwardNeighbour = if (map[posForward] != Tile.Wall) {
					listOf(posForward to last.second)
				} else emptyList<Pair<Cord2D<Int>, Direction>>()

				turns + forwardNeighbour
			},
			getWeightBetweenNodes = { a, b ->
				if (a.second != b.second) 1000
				else 1
			},
			max = path.second
		)

		paths
			.flatMap { it.first }
			.map { it.first }
			.distinct()
			.count()
			.solution(2)
	}
}

fun main() = solve<Day16>()
