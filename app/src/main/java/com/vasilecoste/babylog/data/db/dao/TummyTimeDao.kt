package com.vasilecoste.babylog.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.vasilecoste.babylog.data.db.entity.TummyTimeEntry
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

data class DailyTummyTimeTotal(val date: LocalDate, val totalSeconds: Int)

@Dao
interface TummyTimeDao {
    @Insert
    suspend fun insert(entry: TummyTimeEntry): Long

    @Insert
    suspend fun insertAll(entries: List<TummyTimeEntry>)

    @Update
    suspend fun update(entry: TummyTimeEntry)

    @Delete
    suspend fun delete(entry: TummyTimeEntry)

    @Query("DELETE FROM tummy_time_entries WHERE babyId = :babyId")
    suspend fun deleteAllForBaby(babyId: Long)

    @Query("SELECT * FROM tummy_time_entries WHERE babyId = :babyId AND date = :date ORDER BY startTime DESC")
    fun getForDay(babyId: Long, date: LocalDate): Flow<List<TummyTimeEntry>>

    @Query("SELECT * FROM tummy_time_entries WHERE babyId = :babyId ORDER BY date ASC, startTime ASC")
    suspend fun getAllForBaby(babyId: Long): List<TummyTimeEntry>

    @Query("SELECT DISTINCT date FROM tummy_time_entries WHERE babyId = :babyId ORDER BY date DESC")
    fun getDistinctDates(babyId: Long): Flow<List<LocalDate>>

    @Query(
        "SELECT date, COALESCE(SUM(durationSeconds), 0) AS totalSeconds FROM tummy_time_entries " +
            "WHERE babyId = :babyId GROUP BY date ORDER BY date ASC",
    )
    fun getDailyTotals(babyId: Long): Flow<List<DailyTummyTimeTotal>>
}
