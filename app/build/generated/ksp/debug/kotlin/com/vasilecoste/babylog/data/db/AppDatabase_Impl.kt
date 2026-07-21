package com.vasilecoste.babylog.`data`.db

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.vasilecoste.babylog.`data`.db.dao.BabyProfileDao
import com.vasilecoste.babylog.`data`.db.dao.BabyProfileDao_Impl
import com.vasilecoste.babylog.`data`.db.dao.DiaperSummaryDao
import com.vasilecoste.babylog.`data`.db.dao.DiaperSummaryDao_Impl
import com.vasilecoste.babylog.`data`.db.dao.EntryDao
import com.vasilecoste.babylog.`data`.db.dao.EntryDao_Impl
import com.vasilecoste.babylog.`data`.db.dao.WeightDao
import com.vasilecoste.babylog.`data`.db.dao.WeightDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _babyProfileDao: Lazy<BabyProfileDao> = lazy {
    BabyProfileDao_Impl(this)
  }

  private val _entryDao: Lazy<EntryDao> = lazy {
    EntryDao_Impl(this)
  }

  private val _weightDao: Lazy<WeightDao> = lazy {
    WeightDao_Impl(this)
  }

  private val _diaperSummaryDao: Lazy<DiaperSummaryDao> = lazy {
    DiaperSummaryDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(2, "1510a4af81fe8f5f08f44bc048237466", "8d18cc89f2177456dc1a104d0c098590") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `baby_profiles` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `birthDate` TEXT, `createdAtEpochMillis` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `babyId` INTEGER NOT NULL, `date` TEXT NOT NULL, `time` TEXT NOT NULL, `foodMl` INTEGER, `poop` INTEGER NOT NULL, `pee` INTEGER NOT NULL, `puke` INTEGER NOT NULL, `vitamin` INTEGER NOT NULL, `breastfed` INTEGER NOT NULL, FOREIGN KEY(`babyId`) REFERENCES `baby_profiles`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_entries_babyId` ON `entries` (`babyId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_entries_babyId_date` ON `entries` (`babyId`, `date`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `weight_records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `babyId` INTEGER NOT NULL, `date` TEXT NOT NULL, `weightKg` REAL NOT NULL, `heightCm` REAL, FOREIGN KEY(`babyId`) REFERENCES `baby_profiles`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_weight_records_babyId` ON `weight_records` (`babyId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_weight_records_babyId_date` ON `weight_records` (`babyId`, `date`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `diaper_summaries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `babyId` INTEGER NOT NULL, `date` TEXT NOT NULL, `poopCount` INTEGER NOT NULL, `peeCount` INTEGER NOT NULL, FOREIGN KEY(`babyId`) REFERENCES `baby_profiles`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_diaper_summaries_babyId` ON `diaper_summaries` (`babyId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_diaper_summaries_babyId_date` ON `diaper_summaries` (`babyId`, `date`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '1510a4af81fe8f5f08f44bc048237466')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `baby_profiles`")
        connection.execSQL("DROP TABLE IF EXISTS `entries`")
        connection.execSQL("DROP TABLE IF EXISTS `weight_records`")
        connection.execSQL("DROP TABLE IF EXISTS `diaper_summaries`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        connection.execSQL("PRAGMA foreign_keys = ON")
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsBabyProfiles: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBabyProfiles.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBabyProfiles.put("name", TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBabyProfiles.put("birthDate", TableInfo.Column("birthDate", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBabyProfiles.put("createdAtEpochMillis", TableInfo.Column("createdAtEpochMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBabyProfiles: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesBabyProfiles: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoBabyProfiles: TableInfo = TableInfo("baby_profiles", _columnsBabyProfiles, _foreignKeysBabyProfiles, _indicesBabyProfiles)
        val _existingBabyProfiles: TableInfo = read(connection, "baby_profiles")
        if (!_infoBabyProfiles.equals(_existingBabyProfiles)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |baby_profiles(com.vasilecoste.babylog.data.db.entity.BabyProfile).
              | Expected:
              |""".trimMargin() + _infoBabyProfiles + """
              |
              | Found:
              |""".trimMargin() + _existingBabyProfiles)
        }
        val _columnsEntries: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsEntries.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEntries.put("babyId", TableInfo.Column("babyId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEntries.put("date", TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEntries.put("time", TableInfo.Column("time", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEntries.put("foodMl", TableInfo.Column("foodMl", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEntries.put("poop", TableInfo.Column("poop", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEntries.put("pee", TableInfo.Column("pee", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEntries.put("puke", TableInfo.Column("puke", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEntries.put("vitamin", TableInfo.Column("vitamin", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEntries.put("breastfed", TableInfo.Column("breastfed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysEntries: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysEntries.add(TableInfo.ForeignKey("baby_profiles", "CASCADE", "NO ACTION", listOf("babyId"), listOf("id")))
        val _indicesEntries: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesEntries.add(TableInfo.Index("index_entries_babyId", false, listOf("babyId"), listOf("ASC")))
        _indicesEntries.add(TableInfo.Index("index_entries_babyId_date", false, listOf("babyId", "date"), listOf("ASC", "ASC")))
        val _infoEntries: TableInfo = TableInfo("entries", _columnsEntries, _foreignKeysEntries, _indicesEntries)
        val _existingEntries: TableInfo = read(connection, "entries")
        if (!_infoEntries.equals(_existingEntries)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |entries(com.vasilecoste.babylog.data.db.entity.Entry).
              | Expected:
              |""".trimMargin() + _infoEntries + """
              |
              | Found:
              |""".trimMargin() + _existingEntries)
        }
        val _columnsWeightRecords: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsWeightRecords.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWeightRecords.put("babyId", TableInfo.Column("babyId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWeightRecords.put("date", TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWeightRecords.put("weightKg", TableInfo.Column("weightKg", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWeightRecords.put("heightCm", TableInfo.Column("heightCm", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysWeightRecords: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysWeightRecords.add(TableInfo.ForeignKey("baby_profiles", "CASCADE", "NO ACTION", listOf("babyId"), listOf("id")))
        val _indicesWeightRecords: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesWeightRecords.add(TableInfo.Index("index_weight_records_babyId", false, listOf("babyId"), listOf("ASC")))
        _indicesWeightRecords.add(TableInfo.Index("index_weight_records_babyId_date", false, listOf("babyId", "date"), listOf("ASC", "ASC")))
        val _infoWeightRecords: TableInfo = TableInfo("weight_records", _columnsWeightRecords, _foreignKeysWeightRecords, _indicesWeightRecords)
        val _existingWeightRecords: TableInfo = read(connection, "weight_records")
        if (!_infoWeightRecords.equals(_existingWeightRecords)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |weight_records(com.vasilecoste.babylog.data.db.entity.WeightRecord).
              | Expected:
              |""".trimMargin() + _infoWeightRecords + """
              |
              | Found:
              |""".trimMargin() + _existingWeightRecords)
        }
        val _columnsDiaperSummaries: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDiaperSummaries.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDiaperSummaries.put("babyId", TableInfo.Column("babyId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDiaperSummaries.put("date", TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDiaperSummaries.put("poopCount", TableInfo.Column("poopCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDiaperSummaries.put("peeCount", TableInfo.Column("peeCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDiaperSummaries: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysDiaperSummaries.add(TableInfo.ForeignKey("baby_profiles", "CASCADE", "NO ACTION", listOf("babyId"), listOf("id")))
        val _indicesDiaperSummaries: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesDiaperSummaries.add(TableInfo.Index("index_diaper_summaries_babyId", false, listOf("babyId"), listOf("ASC")))
        _indicesDiaperSummaries.add(TableInfo.Index("index_diaper_summaries_babyId_date", false, listOf("babyId", "date"), listOf("ASC", "ASC")))
        val _infoDiaperSummaries: TableInfo = TableInfo("diaper_summaries", _columnsDiaperSummaries, _foreignKeysDiaperSummaries, _indicesDiaperSummaries)
        val _existingDiaperSummaries: TableInfo = read(connection, "diaper_summaries")
        if (!_infoDiaperSummaries.equals(_existingDiaperSummaries)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |diaper_summaries(com.vasilecoste.babylog.data.db.entity.DiaperSummary).
              | Expected:
              |""".trimMargin() + _infoDiaperSummaries + """
              |
              | Found:
              |""".trimMargin() + _existingDiaperSummaries)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "baby_profiles", "entries", "weight_records", "diaper_summaries")
  }

  public override fun clearAllTables() {
    super.performClear(true, "baby_profiles", "entries", "weight_records", "diaper_summaries")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(BabyProfileDao::class, BabyProfileDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(EntryDao::class, EntryDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(WeightDao::class, WeightDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(DiaperSummaryDao::class, DiaperSummaryDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun babyProfileDao(): BabyProfileDao = _babyProfileDao.value

  public override fun entryDao(): EntryDao = _entryDao.value

  public override fun weightDao(): WeightDao = _weightDao.value

  public override fun diaperSummaryDao(): DiaperSummaryDao = _diaperSummaryDao.value
}
