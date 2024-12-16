package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Cord2D
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
		val position: Cord2D<Int>,
		val velocity: Cord2D<Int>,
	) {
		fun move(bounds: Cord2D<Int>): Robot = this.copy(position = (this.position + this.velocity) % bounds)

		fun moveXSteps(
			bounds: Cord2D<Int>,
			steps: Int,
		): Robot = this.copy(position = ((this.position + this.velocity * steps) % bounds))
	}

	fun List<Robot>.countInBounds(
		start: Cord2D<Int>,
		end: Cord2D<Int>,
	): Int =
		this.count {
			it.position.x > start.x &&
				it.position.x < end.x &&
				it.position.y > start.y &&
				it.position.y < end.y
		}

	fun List<Robot>.printMap(bounds: Cord2D<Int>) {
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
					Cord2D(xStr.toInt(), yStr.toInt()),
					Cord2D(xVelStr.toInt(), yVelStr.toInt()),
				)
			}
	}

	override fun solvePart1() {
		val bounds = Cord2D(101, 103)
		start
			.let {
				(1..100).fold(it) { acc, i ->
					acc.map { it.move(bounds) }
				}
			}.let { robots ->
				listOf(
					Cord2D(-1, -1) to Cord2D(bounds.x / 2, bounds.y / 2),
					Cord2D(bounds.x / 2, -1) to Cord2D(bounds.x, bounds.y / 2),
					Cord2D(-1, bounds.y / 2) to Cord2D(bounds.x / 2, bounds.y),
					Cord2D(bounds.x / 2, bounds.y / 2) to bounds,
				).map {
					robots.countInBounds(it.first, it.second).toLong()
				}.reduce { acc, it -> acc * it }
			}.solution(1)
	}

	fun List<Robot>.areInOneShape(): Boolean {
		val shapes = mutableListOf<Set<Cord2D<Int>>>()
		val map =
			this
				.groupBy { it.position }
				.toMutableMap()
		while (map.isNotEmpty()) {
			val queue = ArrayDeque<Cord2D<Int>>()
			val seen = mutableSetOf<Cord2D<Int>>()
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

	fun Robot.findCycle(bounds: Cord2D<Int>): Long {
		val x = findCycle(this.position.x, this.velocity.x, bounds.x)
		val y = findCycle(this.position.y, this.velocity.y, bounds.y)

		return lcm(x, y)
	}

	override fun solvePart2() {
		val bounds = Cord2D(101, 103)

		val step = generateSequence(1) {
			it + 1
		}.dropWhile { steps ->
			val map = start.map { it.moveXSteps(bounds, steps) }

			!map.areInOneShape()
		}.first()

//		start.map { it.moveXSteps(bounds, step) }
//			.printMap(bounds)

		step
			.solution(2)
	}
}

private operator fun Cord2D<Int>.times(steps: Int): Cord2D<Int> = Cord2D(this.x * steps, this.y * steps)

private operator fun Cord2D<Int>.rem(bounds: Cord2D<Int>): Cord2D<Int> {


	var y = (this.y) % bounds.y
	var x = (this.x) % bounds.x
	if (x < 0) x = x + bounds.x
	if (y < 0) y = y + bounds.y
	return Cord2D(x, y)
}

fun main() = solve<Day14>()
