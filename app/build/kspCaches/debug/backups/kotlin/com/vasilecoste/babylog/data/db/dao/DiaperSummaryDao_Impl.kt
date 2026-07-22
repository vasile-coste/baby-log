package com.vasilecoste.babylog.`data`.db.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.vasilecoste.babylog.`data`.db.Converters
import com.vasilecoste.babylog.`data`.db.entity.DiaperSummary
import java.time.LocalDate
import javax.`annotation`.processing.Generated
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
public class DiaperSummaryDao_Impl(
  __db: RoomDatabase,
) : DiaperSummaryDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfDiaperSummary: EntityInsertAdapter<DiaperSummary>

  private val __converters: Converters = Converters()
  init {
    this.__db = __db
    this.__insertAdapterOfDiaperSummary = object : EntityInsertAdapter<DiaperSummary>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `diaper_summaries` (`id`,`babyId`,`date`,`poopCount`,`peeCount`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DiaperSummary) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.babyId)
        val _tmp: String? = __converters.fromLocalDate(entity.date)
        if (_tmp == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmp)
        }
        statement.bindLong(4, entity.poopCount.toLong())
        statement.bindLong(5, entity.peeCount.toLong())
      }
    }
  }

  public override suspend fun insert(summary: DiaperSummary): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfDiaperSummary.insertAndReturnId(_connection, summary)
    _result
  }

  public override suspend fun insertAll(summaries: List<DiaperSummary>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfDiaperSummary.insert(_connection, summaries)
  }

  public override fun getForDay(babyId: Long, date: LocalDate): Flow<DiaperSummary?> {
    val _sql: String = "SELECT * FROM diaper_summaries WHERE babyId = ? AND date = ? LIMIT 1"
    return createFlow(__db, false, arrayOf("diaper_summaries")) { _connection ->
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
        val _columnIndexOfPoopCount: Int = getColumnIndexOrThrow(_stmt, "poopCount")
        val _columnIndexOfPeeCount: Int = getColumnIndexOrThrow(_stmt, "peeCount")
        val _result: DiaperSummary?
        if (_stmt.step()) {
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
          val _tmpPoopCount: Int
          _tmpPoopCount = _stmt.getLong(_columnIndexOfPoopCount).toInt()
          val _tmpPeeCount: Int
          _tmpPeeCount = _stmt.getLong(_columnIndexOfPeeCount).toInt()
          _result = DiaperSummary(_tmpId,_tmpBabyId,_tmpDate,_tmpPoopCount,_tmpPeeCount)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllForBaby(babyId: Long): List<DiaperSummary> {
    val _sql: String = "SELECT * FROM diaper_summaries WHERE babyId = ? ORDER BY date ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, babyId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfBabyId: Int = getColumnIndexOrThrow(_stmt, "babyId")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfPoopCount: Int = getColumnIndexOrThrow(_stmt, "poopCount")
        val _columnIndexOfPeeCount: Int = getColumnIndexOrThrow(_stmt, "peeCount")
        val _result: MutableList<DiaperSummary> = mutableListOf()
        while (_stmt.step()) {
          val _item: DiaperSummary
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
          val _tmpPoopCount: Int
          _tmpPoopCount = _stmt.getLong(_columnIndexOfPoopCount).toInt()
          val _tmpPeeCount: Int
          _tmpPeeCount = _stmt.getLong(_columnIndexOfPeeCount).toInt()
          _item = DiaperSummary(_tmpId,_tmpBabyId,_tmpDate,_tmpPoopCount,_tmpPeeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getDistinctDates(babyId: Long): Flow<List<LocalDate>> {
    val _sql: String = "SELECT DISTINCT date FROM diaper_summaries WHERE babyId = ?"
    return createFlow(__db, false, arrayOf("diaper_summaries")) { _connection ->
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

  public override suspend fun deleteAllForBaby(babyId: Long) {
    val _sql: String = "DELETE FROM diaper_summaries WHERE babyId = ?"
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
