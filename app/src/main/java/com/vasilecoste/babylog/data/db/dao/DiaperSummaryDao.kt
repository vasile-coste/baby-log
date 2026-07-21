package com.vasilecoste.babylog.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.vasilecoste.babylog.data.db.entity.DiaperSummary
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaperSummaryDao {
    @Insert
    suspend fun insert(summary: DiaperSummary): Long

    @Insert
    suspend fun insertAll(summaries: List<DiaperSummary>)

    @Query("SELECT * FROM diaper_summaries WHERE babyId = :babyId AND date = :date LIMIT 1")
    fun getForDay(babyId: Long, date: LocalDate): Flow<DiaperSummary?>

    @Query("SELECT * FROM diaper_summaries WHERE babyId = :babyId ORDER BY date ASC")
    suspend fun getAllForBaby(babyId: Long): List<DiaperSummary>

    @Query("SELECT DISTINCT date FROM diaper_summaries WHERE babyId = :babyId")
    fun getDistinctDates(babyId: Long): Flow<List<LocalDate>>
}
