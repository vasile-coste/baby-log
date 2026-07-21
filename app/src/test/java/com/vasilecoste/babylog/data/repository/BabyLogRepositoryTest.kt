package com.vasilecoste.babylog.data.repository

import com.vasilecoste.babylog.data.db.dao.DailyFoodTotal
import com.vasilecoste.babylog.data.db.entity.WeightRecord
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BabyLogRepositoryTest {

    private val day1 = LocalDate.of(2026, 7, 1)
    private val day2 = LocalDate.of(2026, 7, 2)
    private val day3 = LocalDate.of(2026, 7, 3)

    @Test
    fun `merges food totals and weights for the same day`() {
        val foodTotals = listOf(DailyFoodTotal(day1, 500), DailyFoodTotal(day2, 750))
        val weights = listOf(WeightRecord(babyId = 1, date = day1, weightKg = 5.1, heightCm = 58.0))

        val result = buildDailyAggregates(foodTotals, weights)

        assertEquals(2, result.size)
        assertEquals(DailyAggregate(day1, 500, 5.1, 58.0), result[0])
        assertEquals(DailyAggregate(day2, 750, null, null), result[1])
    }

    @Test
    fun `includes days with only a weight record and no food entries`() {
        val foodTotals = emptyList<DailyFoodTotal>()
        val weights = listOf(WeightRecord(babyId = 1, date = day3, weightKg = 6.2, heightCm = null))

        val result = buildDailyAggregates(foodTotals, weights)

        assertEquals(1, result.size)
        assertEquals(day3, result[0].date)
        assertEquals(0, result[0].totalFoodMl)
        assertEquals(6.2, result[0].weightKg)
        assertNull(result[0].heightCm)
    }

    @Test
    fun `latest weight record wins when a day has more than one`() {
        val weights = listOf(
            WeightRecord(id = 1, babyId = 1, date = day1, weightKg = 5.0, heightCm = 57.0),
            WeightRecord(id = 2, babyId = 1, date = day1, weightKg = 5.2, heightCm = 57.5),
        )

        val result = buildDailyAggregates(emptyList(), weights)

        assertEquals(1, result.size)
        assertEquals(5.2, result[0].weightKg)
        assertEquals(57.5, result[0].heightCm)
    }

    @Test
    fun `returns dates sorted ascending`() {
        val foodTotals = listOf(DailyFoodTotal(day3, 100), DailyFoodTotal(day1, 200))

        val result = buildDailyAggregates(foodTotals, emptyList())

        assertEquals(listOf(day1, day3), result.map { it.date })
    }
}
