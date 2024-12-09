package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.repeatToSequence
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day9 : Day {
    override val day = 9

    override fun solvePart1() {
        val tape =
            loadInput()
                .first()
                .flatMapIndexed { index, c ->
                    if (index % 2 == 0) {
                        // file
                        (index / 2).repeatToSequence(c.toString().toInt()).toList()
                    } else {
                        listOf(null)
                            .repeatToSequence(c.toString().toInt())
                            .flatten()
                            .toList()
                    }
                }.toMutableList()

        var start = 0
        var end = tape.size - 1
        while (end > start) {
            if (tape[end] == null) {
                // empty
                end--
                continue
            }

            while (start < end && tape[start] != null) start++
            if (start >= end) break
            tape[start] = tape[end]
            tape[end] = null
            end--
        }
        tape
            .map { it?.toLong() }
            .mapIndexed { index, i ->
                i?.let { index.toLong() * it } ?: 0
            }.sum()
            .solution(1)
    }

    fun List<Pair<Int, Int?>>.findSpotForFile(file: Pair<Int, Int>): List<Pair<Int, Int?>> {
        for (index in this.indices) {
            // nothing before to compact found
            val current = this[index]
            if (current.second == file.second) return this

            if (current.second != null) continue

            if (current.first < file.first) continue

            val suffixSpace =
                if (current.first == file.first) {
                    emptyList<Pair<Int, Int?>>()
                } else {
                    listOf((current.first - file.first) to null)
                }
            return this.take(index) + file + suffixSpace +
                this
                    .drop(index + 1)
                    .map {
                        if (it.second != file.second) {
                            it
                        } else {
                            it.first to null
                        }
                    }
        }
        return this
    }

    override fun solvePart2() {
        val tape =
            loadInput()
                .first()
                .mapIndexed { index, c ->
                    val length = c.toString().toInt()
                    if (index % 2 == 0) {
                        // file
                        length to (index / 2)
                    } else {
                        length to null
                    }
                }

        tape
            .reversed()
            .mapNotNull { it.takeIf { it.second != null } as? Pair<Int, Int>? }
            .fold(tape) { tape, file ->
                tape.findSpotForFile(file)
            }.flatMap { (length, id) ->
                id
                    .repeatToSequence(length)
                    .toList()
            }.map { it?.toLong() }
            .mapIndexed { index, i ->
                i?.let { index.toLong() * it } ?: 0
            }.sum()
            .solution(2)
    }
}

fun main() = solve<Day9>()
