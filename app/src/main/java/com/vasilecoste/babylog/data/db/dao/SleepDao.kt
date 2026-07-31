package com.vasilecoste.babylog.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.vasilecoste.babylog.data.db.entity.SleepEntry
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

data class DailySleepTotal(val date: LocalDate, val totalMinutes: Int)

@Dao
interface SleepDao {
    @Insert
    suspend fun insert(entry: SleepEntry): Long

    @Insert
    suspend fun insertAll(entries: List<SleepEntry>)

    @Update
    suspend fun update(entry: SleepEntry)

    @Delete
    suspend fun delete(entry: SleepEntry)

    @Query("DELETE FROM sleep_entries WHERE babyId = :babyId")
    suspend fun deleteAllForBaby(babyId: Long)

    @Query("SELECT * FROM sleep_entries WHERE babyId = :babyId AND date = :date ORDER BY startTime DESC")
    fun getForDay(babyId: Long, date: LocalDate): Flow<List<SleepEntry>>

    @Query("SELECT * FROM sleep_entries WHERE babyId = :babyId ORDER BY date ASC, startTime ASC")
    suspend fun getAllForBaby(babyId: Long): List<SleepEntry>

    @Query("SELECT DISTINCT date FROM sleep_entries WHERE babyId = :babyId ORDER BY date DESC")
    fun getDistinctDates(babyId: Long): Flow<List<LocalDate>>

    @Query(
        "SELECT date, COALESCE(SUM(durationMinutes), 0) AS totalMinutes FROM sleep_entries " +
            "WHERE babyId = :babyId GROUP BY date ORDER BY date ASC",
    )
    fun getDailyTotals(babyId: Long): Flow<List<DailySleepTotal>>
}
