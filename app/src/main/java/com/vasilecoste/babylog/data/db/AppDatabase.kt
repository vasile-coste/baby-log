package com.vasilecoste.babylog.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vasilecoste.babylog.data.db.dao.BabyProfileDao
import com.vasilecoste.babylog.data.db.dao.DiaperSummaryDao
import com.vasilecoste.babylog.data.db.dao.EntryDao
import com.vasilecoste.babylog.data.db.dao.TummyTimeDao
import com.vasilecoste.babylog.data.db.dao.WeightDao
import com.vasilecoste.babylog.data.db.entity.BabyProfile
import com.vasilecoste.babylog.data.db.entity.DiaperSummary
import com.vasilecoste.babylog.data.db.entity.Entry
import com.vasilecoste.babylog.data.db.entity.TummyTimeEntry
import com.vasilecoste.babylog.data.db.entity.WeightRecord

@Database(
    entities = [BabyProfile::class, Entry::class, WeightRecord::class, DiaperSummary::class, TummyTimeEntry::class],
    version = 4,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun babyProfileDao(): BabyProfileDao
    abstract fun entryDao(): EntryDao
    abstract fun weightDao(): WeightDao
    abstract fun diaperSummaryDao(): DiaperSummaryDao
    abstract fun tummyTimeDao(): TummyTimeDao
}
