package me.reckter.aoc.cords.d2

import java.lang.Integer.max
import kotlin.math.absoluteValue
import kotlin.math.sign

/**
 *           2D
 */

data class Coord2D<T : Number>(
    val x: T,
    val y: T,
)

fun Coord2D<Int>.modulo(other: Coord2D<Int>): Coord2D<Int> {
    val x = this.x % other.x
    val y = this.y % other.y
    return Coord2D(
        if (x < 0) x + other.x else x,
        if (y < 0) y + other.y else y,
    )
}

operator fun Coord2D<Int>.plus(other: Coord2D<Int>): Coord2D<Int> =
    Coord2D(
        this.x + other.x,
        this.y + other.y,
    )

operator fun Coord2D<Int>.minus(other: Coord2D<Int>): Coord2D<Int> =
    Coord2D(
        this.x - other.x,
        this.y - other.y,
    )

fun Coord2D<Int>.getNeighbors(noEdges: Boolean = false): List<Coord2D<Int>> {
    if (noEdges) {
        return listOf(
            0 to -1,
            0 to 1,
            -1 to 0,
            1 to 0,
        ).map {
            this + Coord2D(it.first, it.second)
        }
    }

    return (-1..1)
        .flatMap { xOffset ->
            (-1..1).map { yOffset ->
                this + Coord2D(xOffset, yOffset)
            }
        }.filter { it != this }
}

fun Coord2D<Int>.lineTo(end: Coord2D<Int>): List<Coord2D<Int>> {
    val result = mutableListOf<Coord2D<Int>>()
    val xDiff = end.x - this.x
    val yDiff = end.y - this.y
    val xDir = xDiff.sign
    val yDir = yDiff.sign
    val xSteps = xDiff.absoluteValue
    val ySteps = yDiff.absoluteValue
    val steps = max(xSteps, ySteps)
    for (i in 0..steps) {
        val x = this.x + (i * xDir * xSteps / steps)
        val y = this.y + (i * yDir * ySteps / steps)
        result.add(Coord2D(x, y))
    }
    return result
}

fun Coord2D<Int>.manhattenDistance(to: Coord2D<Int>): Int = Math.abs(this.x - to.x) + Math.abs(this.y - to.y)
