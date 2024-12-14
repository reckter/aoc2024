package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Cord2D
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.splitAt

class Day13 : Day {
    override val day = 13

    data class Machine(
        val buttons: List<Pair<Int, Cord2D<Long>>>,
        val goal: Cord2D<Long>,
    )

    operator fun Cord2D<Long>.plus(other: Cord2D<Long>): Cord2D<Long> =
        Cord2D(
            this.x + other.x,
            this.y + other.y,
        )

    fun Machine.findCheapestButtonPressesFast(): Long? {
		/*
		gx = a * ax + b * bx
		gy = a * ay + b * by

		gx * ay - gy * ax = b *bx * ay - b *by * ax
		gx * ay - gy * ax = b (bx * ay - by * ax)

		(gx * ay - gy * ax) / (bx * ay - by * ax) = b


		gx - b * bx = a * ax
		(gx - b * bx) / ax = a
		 */
        val (buttonA, buttonB) =
            this.buttons
                .map { it.second }
        val goal = this.goal

        val denominator = (buttonB.x * buttonA.y - buttonB.y * buttonA.x)
        if (denominator == 0L) return null
        val b = (goal.x * buttonA.y - goal.y * buttonA.x) / denominator
        val a = (goal.x - b * buttonB.x) / buttonA.x

        if (a < 0 || b < 0) return null

        // If there is no real number solution, there stil will be an unreal number solution
        // because we use floats, these equations will no longer be true then
        if (a * buttonA.x + b * buttonB.x != goal.x) return null
        if (a * buttonA.y + b * buttonB.y != goal.y) return null

        return a * 3 + b * 1
    }

    val machines by lazy {
        loadInput(trim = false).splitAt { it.isEmpty() }.map {
            val a =
                3 to (
                    "Button A: X\\+(\\d+), Y\\+(\\d+)"
                        .toRegex()
                        .matchEntire(it.first())
                        ?.destructured
                        ?.let { (xStr, yStr) ->
                            Cord2D(xStr.toLong(), yStr.toLong())
                        } ?: error("No A Button $it")
                )

            val b =
                1 to (
                    "Button B: X\\+(\\d+), Y\\+(\\d+)"
                        .toRegex()
                        .matchEntire(it.toList()[1])
                        ?.destructured
                        ?.let { (xStr, yStr) ->
                            Cord2D(xStr.toLong(), yStr.toLong())
                        } ?: error("No B Button")
                )

            val price =
                "Prize: X=(\\d+), Y=(\\d+)".toRegex().matchEntire(it.last())?.destructured?.let { (xStr, yStr) ->
                    Cord2D(xStr.toLong(), yStr.toLong())
                } ?: error("no price")

            Machine(
                listOf(a, b),
                price,
            )
        }
    }

    override fun solvePart1() {
        machines
            .mapNotNull { it.findCheapestButtonPressesFast() }
            .sum()
            .solution(1)
    }

    override fun solvePart2() {
        val correction = Cord2D(10000000000000L, 10000000000000L)
        machines
            .map { it.copy(goal = it.goal + correction) }
            .mapNotNull { it.findCheapestButtonPressesFast() }
            .sum()
            .solution(2)
    }
}

fun main() = solve<Day13>()
