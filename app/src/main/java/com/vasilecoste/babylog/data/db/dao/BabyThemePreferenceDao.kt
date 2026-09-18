package com.vasilecoste.babylog.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.vasilecoste.babylog.data.db.entity.BabyThemePreference
import kotlinx.coroutines.flow.Flow

@Dao
interface BabyThemePreferenceDao {
    @Upsert
    suspend fun upsert(preference: BabyThemePreference)

    @Query("SELECT * FROM baby_theme_preferences WHERE babyId = :babyId LIMIT 1")
    fun getByBabyId(babyId: Long): Flow<BabyThemePreference?>
}
