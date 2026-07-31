package com.vasilecoste.babylog.data.repository

import androidx.room.withTransaction
import com.vasilecoste.babylog.data.db.AppDatabase
import com.vasilecoste.babylog.data.db.dao.BabyProfileDao
import com.vasilecoste.babylog.data.db.dao.BabyThemePreferenceDao
import com.vasilecoste.babylog.data.db.dao.DailyFoodTotal
import com.vasilecoste.babylog.data.db.dao.DailyTummyTimeTotal
import com.vasilecoste.babylog.data.db.dao.DiaperSummaryDao
import com.vasilecoste.babylog.data.db.dao.EntryDao
import com.vasilecoste.babylog.data.db.dao.SleepDao
import com.vasilecoste.babylog.data.db.dao.TummyTimeDao
import com.vasilecoste.babylog.data.db.dao.WeightDao
import com.vasilecoste.babylog.data.db.entity.BabyProfile
import com.vasilecoste.babylog.data.db.entity.BabyThemePreference
import com.vasilecoste.babylog.data.db.entity.DiaperSummary
import com.vasilecoste.babylog.data.db.entity.Entry
import com.vasilecoste.babylog.data.db.entity.SleepEntry
import com.vasilecoste.babylog.data.db.entity.TummyTimeEntry
import com.vasilecoste.babylog.data.db.entity.WeightRecord
import com.vasilecoste.babylog.data.model.ExportedBabyData
import com.vasilecoste.babylog.data.model.ImportedBabyData
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

data class DailyAggregate(
    val date: LocalDate,
    val totalFoodMl: Int,
    val weightKg: Double?,
    val heightCm: Double?,
    val tummyTimeSeconds: Int = 0,
)

enum class ImportMode { REPLACE, MERGE }

class BabyLogRepository(
    private val database: AppDatabase,
    private val babyProfileDao: BabyProfileDao,
    private val entryDao: EntryDao,
    private val weightDao: WeightDao,
    private val diaperSummaryDao: DiaperSummaryDao,
    private val tummyTimeDao: TummyTimeDao,
    private val babyThemePreferenceDao: BabyThemePreferenceDao,
    private val sleepDao: SleepDao,
) {
    val babies: Flow<List<BabyProfile>> = babyProfileDao.getAll()

    suspend fun getBabyById(id: Long): BabyProfile? = babyProfileDao.getById(id)

    suspend fun addBabyProfile(name: String, birthDate: LocalDate? = null, gender: String? = null): Long = babyProfileDao.insert(
        BabyProfile(
            name = name,
            birthDate = birthDate,
            gender = gender,
            createdAtEpochMillis = System.currentTimeMillis()
        ),
    )

    suspend fun updateBabyProfile(profile: BabyProfile) = babyProfileDao.update(profile)

    suspend fun deleteBabyProfile(profile: BabyProfile) = babyProfileDao.delete(profile)

    fun themeOverride(babyId: Long): Flow<String?> =
        babyThemePreferenceDao.getByBabyId(babyId).map { it?.overrideTheme }

    suspend fun setThemeOverride(babyId: Long, overrideTheme: String?) {
        babyThemePreferenceDao.upsert(BabyThemePreference(babyId = babyId, overrideTheme = overrideTheme))
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

    suspend fun updateWeightRecord(record: WeightRecord) = weightDao.update(record)

    suspend fun deleteWeightRecord(record: WeightRecord) = weightDao.delete(record)

    fun weightsForBaby(babyId: Long): Flow<List<WeightRecord>> = weightDao.getForBaby(babyId)

    fun tummyTimeForDay(babyId: Long, date: LocalDate): Flow<List<TummyTimeEntry>> =
        tummyTimeDao.getForDay(babyId, date)

    suspend fun addTummyTimeEntry(babyId: Long, date: LocalDate, startTime: LocalTime, durationSeconds: Int) {
        tummyTimeDao.insert(
            TummyTimeEntry(babyId = babyId, date = date, startTime = startTime, durationSeconds = durationSeconds),
        )
    }

    suspend fun updateTummyTimeEntry(entry: TummyTimeEntry) = tummyTimeDao.update(entry)

    suspend fun deleteTummyTimeEntry(entry: TummyTimeEntry) = tummyTimeDao.delete(entry)

    fun sleepForDay(babyId: Long, date: LocalDate): Flow<List<SleepEntry>> =
        sleepDao.getForDay(babyId, date)

    suspend fun addSleepEntry(babyId: Long, date: LocalDate, startTime: LocalTime, endTime: LocalTime?) {
        sleepDao.insert(
            SleepEntry(babyId = babyId, date = date, startTime = startTime, endTime = endTime),
        )
    }

    suspend fun updateSleepEntry(entry: SleepEntry) = sleepDao.update(entry)

    suspend fun deleteSleepEntry(entry: SleepEntry) = sleepDao.delete(entry)

    fun datesWithData(babyId: Long): Flow<List<LocalDate>> =
        combine(
            entryDao.getDistinctDates(babyId),
            weightDao.getDistinctDates(babyId),
            diaperSummaryDao.getDistinctDates(babyId),
            tummyTimeDao.getDistinctDates(babyId),
            sleepDao.getDistinctDates(babyId),
        ) { entryDates, weightDates, summaryDates, tummyDates, sleepDates ->
            (entryDates + weightDates + summaryDates + tummyDates + sleepDates).distinct().sortedDescending()
        }

    fun dailyChartData(babyId: Long): Flow<List<DailyAggregate>> =
        combine(
            entryDao.getDailyFoodTotals(babyId),
            weightDao.getForBaby(babyId),
            tummyTimeDao.getDailyTotals(babyId),
        ) { foodTotals, weights, tummyTotals ->
            buildDailyAggregates(foodTotals, weights, tummyTotals)
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
    ): Long = database.withTransaction {
        val babyId = existingBabyId ?: addBabyProfile(data.babyName, data.birthDate, data.gender)
        if (existingBabyId != null) {
            val existing = babyProfileDao.getById(existingBabyId)
            if (existing != null && (data.birthDate != null || data.gender != null)) {
                babyProfileDao.update(
                    existing.copy(
                        birthDate = data.birthDate ?: existing.birthDate,
                        gender = data.gender ?: existing.gender,
                    ),
                )
            }
        }
        if (existingBabyId != null && mode == ImportMode.REPLACE) {
            entryDao.deleteAllForBaby(babyId)
            weightDao.deleteAllForBaby(babyId)
            diaperSummaryDao.deleteAllForBaby(babyId)
            tummyTimeDao.deleteAllForBaby(babyId)
            sleepDao.deleteAllForBaby(babyId)
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
        tummyTimeDao.insertAll(
            data.tummyTimeEntries.map { t ->
                TummyTimeEntry(babyId = babyId, date = t.date, startTime = t.startTime, durationSeconds = t.durationSeconds)
            },
        )
        sleepDao.insertAll(
            data.sleepEntries.map { s ->
                SleepEntry(babyId = babyId, date = s.date, startTime = s.startTime, endTime = s.endTime)
            },
        )
        babyId
    }

    suspend fun exportBabyData(babyId: Long): ExportedBabyData? {
        val baby = babyProfileDao.getById(babyId) ?: return null
        return ExportedBabyData(
            babyName = baby.name,
            birthDate = baby.birthDate,
            gender = baby.gender,
            entries = entryDao.getAllForBaby(babyId),
            weights = weightDao.getAllForBaby(babyId),
            diaperSummaries = diaperSummaryDao.getAllForBaby(babyId),
            tummyTimeEntries = tummyTimeDao.getAllForBaby(babyId),
            sleepEntries = sleepDao.getAllForBaby(babyId),
        )
    }
}

internal fun buildDailyAggregates(
    foodTotals: List<DailyFoodTotal>,
    weights: List<WeightRecord>,
    tummyTotals: List<DailyTummyTimeTotal> = emptyList(),
): List<DailyAggregate> {
    val weightByDate = LinkedHashMap<LocalDate, Double>()
    val heightByDate = LinkedHashMap<LocalDate, Double>()
    weights.forEach { record ->
        record.weightKg?.let { weightByDate[record.date] = it }
        record.heightCm?.let { heightByDate[record.date] = it }
    }
    val tummyByDate = tummyTotals.associate { it.date to it.totalSeconds }
    val allDates = (foodTotals.map { it.date } + weightByDate.keys + heightByDate.keys + tummyByDate.keys)
        .distinct()
        .sorted()
    val foodByDate = foodTotals.associate { it.date to it.totalMl }
    return allDates.map { date ->
        DailyAggregate(
            date = date,
            totalFoodMl = foodByDate[date] ?: 0,
            weightKg = weightByDate[date],
            heightCm = heightByDate[date],
            tummyTimeSeconds = tummyByDate[date] ?: 0,
        )
    }
}
