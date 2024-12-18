package me.reckter.aoc.cords.d4

data class Coord4D<T : Number>(
    val x: T,
    val y: T,
    val z: T,
    val w: T,
)

operator fun Coord4D<Int>.plus(other: Coord4D<Int>): Coord4D<Int> =
    Coord4D(this.x + other.x, this.y + other.y, this.z + other.z, this.w + other.w)

fun Coord4D<Int>.getNeighbors(): List<Coord4D<Int>> =
    (-1..1)
        .flatMap { xOffset ->
            (-1..1).flatMap { yOffset ->
                (-1..1).flatMap { zOffset ->
                    (-1..1).map { wOffset ->
                        this + Coord4D(xOffset, yOffset, zOffset, wOffset)
                    }
                }
            }
        }.filter { it != this }
