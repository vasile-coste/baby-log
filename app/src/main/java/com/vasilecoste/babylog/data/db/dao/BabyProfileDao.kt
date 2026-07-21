package com.vasilecoste.babylog.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.vasilecoste.babylog.data.db.entity.BabyProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface BabyProfileDao {
    @Insert
    suspend fun insert(profile: BabyProfile): Long

    @Query("SELECT * FROM baby_profiles ORDER BY createdAtEpochMillis ASC")
    fun getAll(): Flow<List<BabyProfile>>
}
