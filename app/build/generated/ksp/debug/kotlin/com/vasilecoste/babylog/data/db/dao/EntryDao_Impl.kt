package com.vasilecoste.babylog.`data`.db.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.vasilecoste.babylog.`data`.db.Converters
import com.vasilecoste.babylog.`data`.db.entity.Entry
import java.time.LocalDate
import java.time.LocalTime
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class EntryDao_Impl(
  __db: RoomDatabase,
) : EntryDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfEntry: EntityInsertAdapter<Entry>

  private val __converters: Converters = Converters()

  private val __deleteAdapterOfEntry: EntityDeleteOrUpdateAdapter<Entry>

  private val __updateAdapterOfEntry: EntityDeleteOrUpdateAdapter<Entry>
  init {
    this.__db = __db
    this.__insertAdapterOfEntry = object : EntityInsertAdapter<Entry>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `entries` (`id`,`babyId`,`date`,`time`,`foodMl`,`poop`,`pee`,`puke`,`vitamin`,`breastfed`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Entry) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.babyId)
        val _tmp: String? = __converters.fromLocalDate(entity.date)
        if (_tmp == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmp)
        }
        val _tmp_1: String? = __converters.fromLocalTime(entity.time)
        if (_tmp_1 == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmp_1)
        }
        val _tmpFoodMl: Int? = entity.foodMl
        if (_tmpFoodMl == null) {
          statement.bindNull(5)
        } else {
          statement.bindLong(5, _tmpFoodMl.toLong())
        }
        val _tmp_2: Int = if (entity.poop) 1 else 0
        statement.bindLong(6, _tmp_2.toLong())
        val _tmp_3: Int = if (entity.pee) 1 else 0
        statement.bindLong(7, _tmp_3.toLong())
        val _tmp_4: Int = if (entity.puke) 1 else 0
        statement.bindLong(8, _tmp_4.toLong())
        val _tmp_5: Int = if (entity.vitamin) 1 else 0
        statement.bindLong(9, _tmp_5.toLong())
        val _tmp_6: Int = if (entity.breastfed) 1 else 0
        statement.bindLong(10, _tmp_6.toLong())
      }
    }
    this.__deleteAdapterOfEntry = object : EntityDeleteOrUpdateAdapter<Entry>() {
      protected override fun createQuery(): String = "DELETE FROM `entries` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Entry) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfEntry = object : EntityDeleteOrUpdateAdapter<Entry>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `entries` SET `id` = ?,`babyId` = ?,`date` = ?,`time` = ?,`foodMl` = ?,`poop` = ?,`pee` = ?,`puke` = ?,`vitamin` = ?,`breastfed` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Entry) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.babyId)
        val _tmp: String? = __converters.fromLocalDate(entity.date)
        if (_tmp == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmp)
        }
        val _tmp_1: String? = __converters.fromLocalTime(entity.time)
        if (_tmp_1 == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmp_1)
        }
        val _tmpFoodMl: Int? = entity.foodMl
        if (_tmpFoodMl == null) {
          statement.bindNull(5)
        } else {
          statement.bindLong(5, _tmpFoodMl.toLong())
        }
        val _tmp_2: Int = if (entity.poop) 1 else 0
        statement.bindLong(6, _tmp_2.toLong())
        val _tmp_3: Int = if (entity.pee) 1 else 0
        statement.bindLong(7, _tmp_3.toLong())
        val _tmp_4: Int = if (entity.puke) 1 else 0
        statement.bindLong(8, _tmp_4.toLong())
        val _tmp_5: Int = if (entity.vitamin) 1 else 0
        statement.bindLong(9, _tmp_5.toLong())
        val _tmp_6: Int = if (entity.breastfed) 1 else 0
        statement.bindLong(10, _tmp_6.toLong())
        statement.bindLong(11, entity.id)
      }
    }
  }

  public override suspend fun insert(entry: Entry): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfEntry.insertAndReturnId(_connection, entry)
    _result
  }

  public override suspend fun insertAll(entries: List<Entry>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfEntry.insert(_connection, entries)
  }

  public override suspend fun delete(entry: Entry): Unit = performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfEntry.handle(_connection, entry)
  }

  public override suspend fun update(entry: Entry): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfEntry.handle(_connection, entry)
  }

  public override fun getForDay(babyId: Long, date: LocalDate): Flow<List<Entry>> {
    val _sql: String = "SELECT * FROM entries WHERE babyId = ? AND date = ? ORDER BY time ASC"
    return createFlow(__db, false, arrayOf("entries")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, babyId)
        _argIndex = 2
        val _tmp: String? = __converters.fromLocalDate(date)
        if (_tmp == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, _tmp)
        }
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfBabyId: Int = getColumnIndexOrThrow(_stmt, "babyId")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfTime: Int = getColumnIndexOrThrow(_stmt, "time")
        val _columnIndexOfFoodMl: Int = getColumnIndexOrThrow(_stmt, "foodMl")
        val _columnIndexOfPoop: Int = getColumnIndexOrThrow(_stmt, "poop")
        val _columnIndexOfPee: Int = getColumnIndexOrThrow(_stmt, "pee")
        val _columnIndexOfPuke: Int = getColumnIndexOrThrow(_stmt, "puke")
        val _columnIndexOfVitamin: Int = getColumnIndexOrThrow(_stmt, "vitamin")
        val _columnIndexOfBreastfed: Int = getColumnIndexOrThrow(_stmt, "breastfed")
        val _result: MutableList<Entry> = mutableListOf()
        while (_stmt.step()) {
          val _item: Entry
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpBabyId: Long
          _tmpBabyId = _stmt.getLong(_columnIndexOfBabyId)
          val _tmpDate: LocalDate
          val _tmp_1: String?
          if (_stmt.isNull(_columnIndexOfDate)) {
            _tmp_1 = null
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfDate)
          }
          val _tmp_2: LocalDate? = __converters.toLocalDate(_tmp_1)
          if (_tmp_2 == null) {
            error("Expected NON-NULL 'java.time.LocalDate', but it was NULL.")
          } else {
            _tmpDate = _tmp_2
          }
          val _tmpTime: LocalTime
          val _tmp_3: String?
          if (_stmt.isNull(_columnIndexOfTime)) {
            _tmp_3 = null
          } else {
            _tmp_3 = _stmt.getText(_columnIndexOfTime)
          }
          val _tmp_4: LocalTime? = __converters.toLocalTime(_tmp_3)
          if (_tmp_4 == null) {
            error("Expected NON-NULL 'java.time.LocalTime', but it was NULL.")
          } else {
            _tmpTime = _tmp_4
          }
          val _tmpFoodMl: Int?
          if (_stmt.isNull(_columnIndexOfFoodMl)) {
            _tmpFoodMl = null
          } else {
            _tmpFoodMl = _stmt.getLong(_columnIndexOfFoodMl).toInt()
          }
          val _tmpPoop: Boolean
          val _tmp_5: Int
          _tmp_5 = _stmt.getLong(_columnIndexOfPoop).toInt()
          _tmpPoop = _tmp_5 != 0
          val _tmpPee: Boolean
          val _tmp_6: Int
          _tmp_6 = _stmt.getLong(_columnIndexOfPee).toInt()
          _tmpPee = _tmp_6 != 0
          val _tmpPuke: Boolean
          val _tmp_7: Int
          _tmp_7 = _stmt.getLong(_columnIndexOfPuke).toInt()
          _tmpPuke = _tmp_7 != 0
          val _tmpVitamin: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfVitamin).toInt()
          _tmpVitamin = _tmp_8 != 0
          val _tmpBreastfed: Boolean
          val _tmp_9: Int
          _tmp_9 = _stmt.getLong(_columnIndexOfBreastfed).toInt()
          _tmpBreastfed = _tmp_9 != 0
          _item = Entry(_tmpId,_tmpBabyId,_tmpDate,_tmpTime,_tmpFoodMl,_tmpPoop,_tmpPee,_tmpPuke,_tmpVitamin,_tmpBreastfed)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllForBaby(babyId: Long): List<Entry> {
    val _sql: String = "SELECT * FROM entries WHERE babyId = ? ORDER BY date ASC, time ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, babyId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfBabyId: Int = getColumnIndexOrThrow(_stmt, "babyId")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfTime: Int = getColumnIndexOrThrow(_stmt, "time")
        val _columnIndexOfFoodMl: Int = getColumnIndexOrThrow(_stmt, "foodMl")
        val _columnIndexOfPoop: Int = getColumnIndexOrThrow(_stmt, "poop")
        val _columnIndexOfPee: Int = getColumnIndexOrThrow(_stmt, "pee")
        val _columnIndexOfPuke: Int = getColumnIndexOrThrow(_stmt, "puke")
        val _columnIndexOfVitamin: Int = getColumnIndexOrThrow(_stmt, "vitamin")
        val _columnIndexOfBreastfed: Int = getColumnIndexOrThrow(_stmt, "breastfed")
        val _result: MutableList<Entry> = mutableListOf()
        while (_stmt.step()) {
          val _item: Entry
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpBabyId: Long
          _tmpBabyId = _stmt.getLong(_columnIndexOfBabyId)
          val _tmpDate: LocalDate
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfDate)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfDate)
          }
          val _tmp_1: LocalDate? = __converters.toLocalDate(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'java.time.LocalDate', but it was NULL.")
          } else {
            _tmpDate = _tmp_1
          }
          val _tmpTime: LocalTime
          val _tmp_2: String?
          if (_stmt.isNull(_columnIndexOfTime)) {
            _tmp_2 = null
          } else {
            _tmp_2 = _stmt.getText(_columnIndexOfTime)
          }
          val _tmp_3: LocalTime? = __converters.toLocalTime(_tmp_2)
          if (_tmp_3 == null) {
            error("Expected NON-NULL 'java.time.LocalTime', but it was NULL.")
          } else {
            _tmpTime = _tmp_3
          }
          val _tmpFoodMl: Int?
          if (_stmt.isNull(_columnIndexOfFoodMl)) {
            _tmpFoodMl = null
          } else {
            _tmpFoodMl = _stmt.getLong(_columnIndexOfFoodMl).toInt()
          }
          val _tmpPoop: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfPoop).toInt()
          _tmpPoop = _tmp_4 != 0
          val _tmpPee: Boolean
          val _tmp_5: Int
          _tmp_5 = _stmt.getLong(_columnIndexOfPee).toInt()
          _tmpPee = _tmp_5 != 0
          val _tmpPuke: Boolean
          val _tmp_6: Int
          _tmp_6 = _stmt.getLong(_columnIndexOfPuke).toInt()
          _tmpPuke = _tmp_6 != 0
          val _tmpVitamin: Boolean
          val _tmp_7: Int
          _tmp_7 = _stmt.getLong(_columnIndexOfVitamin).toInt()
          _tmpVitamin = _tmp_7 != 0
          val _tmpBreastfed: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfBreastfed).toInt()
          _tmpBreastfed = _tmp_8 != 0
          _item = Entry(_tmpId,_tmpBabyId,_tmpDate,_tmpTime,_tmpFoodMl,_tmpPoop,_tmpPee,_tmpPuke,_tmpVitamin,_tmpBreastfed)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getDistinctDates(babyId: Long): Flow<List<LocalDate>> {
    val _sql: String = "SELECT DISTINCT date FROM entries WHERE babyId = ? ORDER BY date DESC"
    return createFlow(__db, false, arrayOf("entries")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, babyId)
        val _result: MutableList<LocalDate> = mutableListOf()
        while (_stmt.step()) {
          val _item: LocalDate
          val _tmp: String?
          if (_stmt.isNull(0)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(0)
          }
          val _tmp_1: LocalDate? = __converters.toLocalDate(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'java.time.LocalDate', but it was NULL.")
          } else {
            _item = _tmp_1
          }
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getDailyFoodTotals(babyId: Long): Flow<List<DailyFoodTotal>> {
    val _sql: String = "SELECT date, COALESCE(SUM(foodMl), 0) AS totalMl FROM entries WHERE babyId = ? GROUP BY date ORDER BY date ASC"
    return createFlow(__db, false, arrayOf("entries")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, babyId)
        val _columnIndexOfDate: Int = 0
        val _columnIndexOfTotalMl: Int = 1
        val _result: MutableList<DailyFoodTotal> = mutableListOf()
        while (_stmt.step()) {
          val _item: DailyFoodTotal
          val _tmpDate: LocalDate
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfDate)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfDate)
          }
          val _tmp_1: LocalDate? = __converters.toLocalDate(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'java.time.LocalDate', but it was NULL.")
          } else {
            _tmpDate = _tmp_1
          }
          val _tmpTotalMl: Int
          _tmpTotalMl = _stmt.getLong(_columnIndexOfTotalMl).toInt()
          _item = DailyFoodTotal(_tmpDate,_tmpTotalMl)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAllForBaby(babyId: Long) {
    val _sql: String = "DELETE FROM entries WHERE babyId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, babyId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
