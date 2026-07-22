package com.vasilecoste.babylog.data.repository

import com.vasilecoste.babylog.data.db.dao.BabyProfileDao
import com.vasilecoste.babylog.data.db.dao.DailyFoodTotal
import com.vasilecoste.babylog.data.db.dao.DiaperSummaryDao
import com.vasilecoste.babylog.data.db.dao.EntryDao
import com.vasilecoste.babylog.data.db.dao.WeightDao
import com.vasilecoste.babylog.data.db.entity.BabyProfile
import com.vasilecoste.babylog.data.db.entity.DiaperSummary
import com.vasilecoste.babylog.data.db.entity.Entry
import com.vasilecoste.babylog.data.db.entity.WeightRecord
import com.vasilecoste.babylog.data.model.ExportedBabyData
import com.vasilecoste.babylog.data.model.ImportedBabyData
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class DailyAggregate(
    val date: LocalDate,
    val totalFoodMl: Int,
    val weightKg: Double?,
    val heightCm: Double?,
)

enum class ImportMode { REPLACE, MERGE }

class BabyLogRepository(
    private val babyProfileDao: BabyProfileDao,
    private val entryDao: EntryDao,
    private val weightDao: WeightDao,
    private val diaperSummaryDao: DiaperSummaryDao,
) {
    val babies: Flow<List<BabyProfile>> = babyProfileDao.getAll()

    suspend fun addBabyProfile(name: String, birthDate: LocalDate? = null): Long = babyProfileDao.insert(
        BabyProfile(name = name, birthDate = birthDate, createdAtEpochMillis = System.currentTimeMillis()),
    )

    suspend fun updateBabyBirthDate(babyId: Long, birthDate: LocalDate) {
        val baby = babyProfileDao.getById(babyId) ?: return
        babyProfileDao.update(baby.copy(birthDate = birthDate))
    }

    fun entriesForDay(babyId: Long, date: LocalDate): Flow<List<Entry>> =
        entryDao.getForDay(babyId, date)

    fun diaperSummaryForDay(babyId: Long, date: LocalDate): Flow<DiaperSummary?> =
        diaperSummaryDao.getForDay(babyId, date)

    suspend fun addEntry(
        babyId: Long,
        date: LocalDate,
        time: LocalTime,
        foodMl: Int?,
        poop: Boolean,
        pee: Boolean,
        puke: Boolean,
        vitamin: Boolean,
        breastfed: Boolean,
    ) {
        entryDao.insert(
            Entry(
                babyId = babyId,
                date = date,
                time = time,
                foodMl = foodMl,
                poop = poop,
                pee = pee,
                puke = puke,
                vitamin = vitamin,
                breastfed = breastfed,
            ),
        )
    }

    suspend fun updateEntry(entry: Entry) = entryDao.update(entry)

    suspend fun deleteEntry(entry: Entry) = entryDao.delete(entry)

    suspend fun addWeight(babyId: Long, date: LocalDate, weightKg: Double?, heightCm: Double?) {
        weightDao.insert(WeightRecord(babyId = babyId, date = date, weightKg = weightKg, heightCm = heightCm))
    }

    fun weightsForBaby(babyId: Long): Flow<List<WeightRecord>> = weightDao.getForBaby(babyId)

    fun datesWithData(babyId: Long): Flow<List<LocalDate>> =
        combine(
            entryDao.getDistinctDates(babyId),
            weightDao.getDistinctDates(babyId),
            diaperSummaryDao.getDistinctDates(babyId),
        ) { entryDates, weightDates, summaryDates ->
            (entryDates + weightDates + summaryDates).distinct().sortedDescending()
        }

    fun dailyChartData(babyId: Long): Flow<List<DailyAggregate>> =
        combine(entryDao.getDailyFoodTotals(babyId), weightDao.getForBaby(babyId)) { foodTotals, weights ->
            buildDailyAggregates(foodTotals, weights)
        }

    /**
     * Imports [data]'s entries/weights/summaries. If [existingBabyId] is null, a new baby profile is
     * created from [data]. Otherwise the data is imported under that existing baby: [ImportMode.REPLACE]
     * first wipes its current entries/weights/summaries, [ImportMode.MERGE] adds to them. Returns the
     * baby's id.
     */
    suspend fun importBabyData(
        data: ImportedBabyData,
        existingBabyId: Long? = null,
        mode: ImportMode = ImportMode.MERGE,
    ): Long {
        val babyId = existingBabyId ?: addBabyProfile(data.babyName)
        if (existingBabyId != null && mode == ImportMode.REPLACE) {
            entryDao.deleteAllForBaby(babyId)
            weightDao.deleteAllForBaby(babyId)
            diaperSummaryDao.deleteAllForBaby(babyId)
        }
        entryDao.insertAll(
            data.entries.map { e ->
                Entry(
                    babyId = babyId,
                    date = e.date,
                    time = e.time,
                    foodMl = e.foodMl,
                    poop = e.poop,
                    pee = e.pee,
                    puke = e.puke,
                    vitamin = e.vitamin,
                    breastfed = e.breastfed,
                )
            },
        )
        weightDao.insertAll(
            data.weights.map { w -> WeightRecord(babyId = babyId, date = w.date, weightKg = w.weightKg, heightCm = w.heightCm) },
        )
        diaperSummaryDao.insertAll(
            data.diaperSummaries.map { s ->
                DiaperSummary(babyId = babyId, date = s.date, poopCount = s.poopCount, peeCount = s.peeCount)
            },
        )
        return babyId
    }

    suspend fun exportBabyData(babyId: Long): ExportedBabyData? {
        val baby = babyProfileDao.getById(babyId) ?: return null
        return ExportedBabyData(
            babyName = baby.name,
            entries = entryDao.getAllForBaby(babyId),
            weights = weightDao.getAllForBaby(babyId),
            diaperSummaries = diaperSummaryDao.getAllForBaby(babyId),
        )
    }
}

internal fun buildDailyAggregates(foodTotals: List<DailyFoodTotal>, weights: List<WeightRecord>): List<DailyAggregate> {
    val weightByDate = LinkedHashMap<LocalDate, Double>()
    val heightByDate = LinkedHashMap<LocalDate, Double>()
    weights.forEach { record ->
        record.weightKg?.let { weightByDate[record.date] = it }
        record.heightCm?.let { heightByDate[record.date] = it }
    }
    val allDates = (foodTotals.map { it.date } + weightByDate.keys + heightByDate.keys).distinct().sorted()
    val foodByDate = foodTotals.associate { it.date to it.totalMl }
    return allDates.map { date ->
        DailyAggregate(
            date = date,
            totalFoodMl = foodByDate[date] ?: 0,
            weightKg = weightByDate[date],
            heightCm = heightByDate[date],
        )
    }
}
