package com.vasilecoste.babylog.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.vasilecoste.babylog.data.db.entity.BabyProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface BabyProfileDao {
    @Insert
    suspend fun insert(profile: BabyProfile): Long

    @Update
    suspend fun update(profile: BabyProfile)

    @Delete
    suspend fun delete(profile: BabyProfile)

    @Query("SELECT * FROM baby_profiles ORDER BY createdAtEpochMillis ASC")
    fun getAll(): Flow<List<BabyProfile>>

    @Query("SELECT * FROM baby_profiles WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): BabyProfile?
}
