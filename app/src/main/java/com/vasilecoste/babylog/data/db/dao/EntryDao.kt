package com.vasilecoste.babylog.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.vasilecoste.babylog.data.db.entity.Entry
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

data class DailyFoodTotal(val date: LocalDate, val totalMl: Int)

@Dao
interface EntryDao {
    @Insert
    suspend fun insert(entry: Entry): Long

    @Update
    suspend fun update(entry: Entry)

    @Delete
    suspend fun delete(entry: Entry)

    @Query("SELECT * FROM entries WHERE babyId = :babyId AND date = :date ORDER BY time ASC")
    fun getForDay(babyId: Long, date: LocalDate): Flow<List<Entry>>

    @Query("SELECT DISTINCT date FROM entries WHERE babyId = :babyId ORDER BY date DESC")
    fun getDistinctDates(babyId: Long): Flow<List<LocalDate>>

    @Query(
        "SELECT date, COALESCE(SUM(foodMl), 0) AS totalMl FROM entries " +
            "WHERE babyId = :babyId GROUP BY date ORDER BY date ASC",
    )
    fun getDailyFoodTotals(babyId: Long): Flow<List<DailyFoodTotal>>
}
