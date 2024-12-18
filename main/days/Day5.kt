package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.replace
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.splitAtEmptyLine
import me.reckter.aoc.toIntegers

class Day5 : Day {
    override val day = 5

    data class Order(
        val first: Int,
        val second: Int,
    )

    val orderings by lazy {
        loadInput(trim = false)
            .splitAtEmptyLine()
            .first()
            .toList()
            .parseWithRegex("(\\d+)\\|(\\d+)")
            .map { (first, second) ->
                Order(first.toInt() ?: 0, second.toInt() ?: 0)
            }
    }

    val updates by lazy {
        loadInput(trim = false)
            .splitAtEmptyLine()
            .last()
            .map { it.split(",").toIntegers() }
    }

    val correctlyOrdered by lazy {
        updates
            .filter { update ->
                orderings.all { order ->
                    if (!update.contains(order.first) || !update.contains(order.second)) {
                        return@all true
                    }
                    update.indexOf(order.first) < update.indexOf(order.second)
                }
            }
    }

    override fun solvePart1() {
        correctlyOrdered
            .sumOf { it[(it.size - 1) / 2] }
            .solution(1)
    }

    fun sortOne(update: List<Int>): List<Int> {
        var cur = update
        var changed = true
        while (changed) {
            changed = false
            for (order in orderings) {
                val firstIndex = cur.indexOf(order.first)
                val secondIndex = cur.indexOf(order.second)

                if (firstIndex == -1 || secondIndex == -1) {
                    continue
                }
                if (firstIndex > secondIndex) {
                    val first = cur[firstIndex]
                    val second = cur[secondIndex]
                    cur =
                        cur
                            .replace(firstIndex, second)
                            .replace(secondIndex, first)
                    changed = true
                }
            }
        }
        return cur
    }

    override fun solvePart2() {
        val notOrdered = updates - correctlyOrdered

        notOrdered
            .map { sortOne(it) }
            .sumOf { it[(it.size - 1) / 2] }
            .solution(2)
    }
}

fun main() = solve<Day5>()
