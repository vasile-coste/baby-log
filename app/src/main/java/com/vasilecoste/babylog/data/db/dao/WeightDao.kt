package com.vasilecoste.babylog.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.vasilecoste.babylog.data.db.entity.WeightRecord
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightDao {
    @Insert
    suspend fun insert(record: WeightRecord): Long

    @Query("SELECT * FROM weight_records WHERE babyId = :babyId ORDER BY date ASC, id ASC")
    fun getForBaby(babyId: Long): Flow<List<WeightRecord>>

    @Query("SELECT DISTINCT date FROM weight_records WHERE babyId = :babyId")
    fun getDistinctDates(babyId: Long): Flow<List<LocalDate>>
}
