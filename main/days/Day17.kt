package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.replace
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.splitAt
import me.reckter.aoc.toIntegers
import kotlin.math.pow

class Day17 : Day {
    override val day = 17

    data class Program(
        val tape: List<Int>,
        val registers: MutableList<Long>,
        var instructionPointer: Int,
        val out: MutableList<Int>,
    )

    fun Program.getComboOperand(): Long {
        val operand = tape[instructionPointer + 1]
        if (operand <= 3) return operand.toLong()
        if (operand >= 7) error("invalid combo operand $operand")
        return registers[operand - 4]
    }

    fun Program.executeOneOperation(): Program {
        val operation = tape[instructionPointer]
        val operand = tape[instructionPointer + 1]
        when (operation) {
            // adv
            0 -> {
                registers[0] = registers[0] / pow(2, getComboOperand())
            }

            // bxl
            1 -> {
                registers[1] = registers[1] xor operand.toLong()
            }

            // bst
            2 -> {
                registers[1] = getComboOperand() % 8L
            }

            // jnz
            3 ->
                if (registers[0] != 0L) {
                    instructionPointer = operand
                    return this
                }

            // bxz
            4 -> {
                registers[1] = registers[1] xor registers[2]
            }

            // out
            5 -> {
                out.add((getComboOperand() % 8).toInt())
            }
            // bdv
            6 -> {
                registers[1] = registers[0] / pow(2, getComboOperand())
            }

            // cdv
            7 -> {
                registers[2] = registers[0] / pow(2, getComboOperand())
            }

            else -> error("Invalid instruction!")
        }
        instructionPointer += 2
        return this
    }

    val registers by lazy {
        loadInput(trim = false)
            .splitAt { it.isEmpty() }
            .first()
            .toList()
            .parseWithRegex("Register .: (\\d+)")
            .map { (it) -> it.toLong() }
    }

    val tape by lazy {
        loadInput(trim = false)
            .splitAt { it.isEmpty() }
            .last()
            .first()
            .removePrefix("Program: ")
            .split(",")
            .toIntegers()
    }

    override fun solvePart1() {
        runProgrammWithRegister(registers)
            .out
            .joinToString(",")
            .solution(1)
    }

    fun runProgrammWithRegister(registers: List<Long>): Program {
        val start = Program(tape, registers.toMutableList(), 0, mutableListOf())

        return generateSequence(start) { it.executeOneOperation() }
            .takeWhile { it.instructionPointer < tape.size }
            .last()
    }

    fun runCompiled(start: Long): List<Int> = runProgrammWithRegister(registers.replace(0, start).toMutableList()).out

    fun getPossibleDigits(
        start: Long,
        end: Long,
        digitAfter: Int,
    ): Sequence<Long> =
        generateSequence(start) { it + 1 }
            .takeWhile { it <= end }
            .filter {
                val res = runCompiled(it)
                res.size == tape.size - digitAfter &&
                    res
                        .zip(tape.drop(digitAfter))
                        .all { (a, b) -> a == b }
            }

    fun guessLastDigits(digit: Int): Sequence<Long> {
        val sequenceOfRest =
            if (digit < tape.size - 1) {
                guessLastDigits(digit + 1)
            } else {
                sequenceOf(0L)
            }

        return sequenceOfRest
            .flatMap {
                getPossibleDigits(it shl 3, (it shl 3) + 7, digit)
            }
    }

    override fun solvePart2() {
        val guess =
            guessLastDigits(0)
                .first()

        guess.solution(2)
    }
}

fun pow(
    a: Int,
    b: Long,
): Long = a.toDouble().pow(b.toDouble()).toLong()

fun main() = solve<Day17>()
