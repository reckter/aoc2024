package me.reckter.aoc.cords.d3

import java.lang.Math.abs

/**
 *           3D
 */

data class Coord3D<T : Number>(
    val x: T,
    val y: T,
    val z: T,
)

operator fun Coord3D<Int>.plus(other: Coord3D<Int>): Coord3D<Int> =
    Coord3D(
        this.x + other.x,
        this.y + other.y,
        this.z + other.z,
    )

fun Coord3D<Int>.getNeighbors(noEdges: Boolean = false): List<Coord3D<Int>> {
    if (noEdges) {
        return listOf(
            Coord3D(0, 0, -1),
            Coord3D(0, 0, 1),
            Coord3D(0, -1, 0),
            Coord3D(0, 1, 0),
            Coord3D(-1, 0, 0),
            Coord3D(1, 0, 0),
        ).map {
            this + it
        }
    }

    return (-1..1)
        .flatMap { xOffset ->
            (-1..1).flatMap { yOffset ->
                (-1..1).map { zOffset ->
                    this + Coord3D(xOffset, yOffset, zOffset)
                }
            }
        }.filter { it != this }
}

fun Coord3D<Int>.manhattenDistance(to: Coord3D<Int>): Int = abs(this.x - to.x) + abs(this.y - to.y) + abs(this.z - to.z)
