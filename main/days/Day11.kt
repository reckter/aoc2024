package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.digits
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.toLongs

class Day11 : Day {
    override val day = 11

    val startStones by lazy {
        loadInput()
            .first()
            .split(" ")
            .toLongs()
            .groupBy { it }
            .mapValues { it.value.size.toLong() }
    }

    private fun blinkOneStone(it: Long) =
        when {
            it == 0L -> listOf(1L)
            it.digits().size % 2 == 0 -> {
                val str = it.toString()
                listOf(
                    str.take(str.length / 2).toLong(),
                    str.drop(str.length / 2).toLong(),
                )
            }

            else -> listOf(it * 2024L)
        }

    fun Map<Long, Long>.blink(): Map<Long, Long> =
        this.entries
            .flatMap { (stone, amount) ->
                val newStones = blinkOneStone(stone)

                newStones.map { it to amount }
            }.groupBy { it.first }
            .mapValues { it.value.sumOf { it.second } }

    fun Map<Long, Long>.blink(times: Int): Map<Long, Long> =
        (1..times)
            .fold(this) { it, _ -> it.blink() }

    override fun solvePart1() {
        startStones
            .blink(25)
            .values
            .sum()
            .solution(1)
    }

    override fun solvePart2() {
        startStones
            .blink(75)
            .values
            .sum()
            .solution(2)
    }
}

fun main() = solve<Day11>()
