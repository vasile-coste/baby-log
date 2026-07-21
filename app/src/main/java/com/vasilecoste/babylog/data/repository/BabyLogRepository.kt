package com.vasilecoste.babylog.data.repository

import com.vasilecoste.babylog.data.db.dao.BabyProfileDao
import com.vasilecoste.babylog.data.db.dao.EntryDao
import com.vasilecoste.babylog.data.db.dao.WeightDao
import com.vasilecoste.babylog.data.db.entity.BabyProfile
import com.vasilecoste.babylog.data.db.entity.Entry
import com.vasilecoste.babylog.data.db.entity.WeightRecord
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class DailyAggregate(
    val date: LocalDate,
    val totalFoodMl: Int,
    val weightKg: Double?,
)

class BabyLogRepository(
    private val babyProfileDao: BabyProfileDao,
    private val entryDao: EntryDao,
    private val weightDao: WeightDao,
) {
    val babies: Flow<List<BabyProfile>> = babyProfileDao.getAll()

    suspend fun addBabyProfile(name: String): Long = babyProfileDao.insert(
        BabyProfile(name = name, createdAtEpochMillis = System.currentTimeMillis()),
    )

    fun entriesForDay(babyId: Long, date: LocalDate): Flow<List<Entry>> =
        entryDao.getForDay(babyId, date)

    suspend fun addEntry(babyId: Long, date: LocalDate, time: LocalTime, foodMl: Int?, poop: Boolean, pee: Boolean, vitamin: Boolean) {
        entryDao.insert(
            Entry(babyId = babyId, date = date, time = time, foodMl = foodMl, poop = poop, pee = pee, vitamin = vitamin),
        )
    }

    suspend fun updateEntry(entry: Entry) = entryDao.update(entry)

    suspend fun deleteEntry(entry: Entry) = entryDao.delete(entry)

    suspend fun addWeight(babyId: Long, date: LocalDate, weightKg: Double) {
        weightDao.insert(WeightRecord(babyId = babyId, date = date, weightKg = weightKg))
    }

    fun weightsForBaby(babyId: Long): Flow<List<WeightRecord>> = weightDao.getForBaby(babyId)

    fun datesWithData(babyId: Long): Flow<List<LocalDate>> =
        combine(entryDao.getDistinctDates(babyId), weightDao.getDistinctDates(babyId)) { entryDates, weightDates ->
            (entryDates + weightDates).distinct().sortedDescending()
        }

    fun dailyChartData(babyId: Long): Flow<List<DailyAggregate>> =
        combine(entryDao.getDailyFoodTotals(babyId), weightDao.getForBaby(babyId)) { foodTotals, weights ->
            val weightByDate = LinkedHashMap<LocalDate, Double>()
            weights.forEach { weightByDate[it.date] = it.weightKg }
            val allDates = (foodTotals.map { it.date } + weightByDate.keys).distinct().sorted()
            val foodByDate = foodTotals.associate { it.date to it.totalMl }
            allDates.map { date ->
                DailyAggregate(
                    date = date,
                    totalFoodMl = foodByDate[date] ?: 0,
                    weightKg = weightByDate[date],
                )
            }
        }
}
