package me.reckter.aoc

import me.reckter.aoc.cords.d2.Coord2D
import me.reckter.aoc.cords.d2.lineTo
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class Coord2DTest {
    @Test
    fun `should correctly calculate lineTo between straight points`() {
        fun testLine(
            start: Coord2D<Int>,
            end: Coord2D<Int>,
            expected: List<Coord2D<Int>>,
        ) {
            assertTrue {
                start.lineTo(end) == expected
            }
        }

        testLine(
            Coord2D(0, 0),
            Coord2D(0, 3),
            listOf(
                Coord2D(0, 0),
                Coord2D(0, 1),
                Coord2D(0, 2),
                Coord2D(0, 3),
            ),
        )

        testLine(
            Coord2D(0, 0),
            Coord2D(3, 0),
            listOf(
                Coord2D(0, 0),
                Coord2D(1, 0),
                Coord2D(2, 0),
                Coord2D(3, 0),
            ),
        )
    }
}
