package com.vasilecoste.babylog.`data`.db.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.vasilecoste.babylog.`data`.db.Converters
import com.vasilecoste.babylog.`data`.db.entity.WeightRecord
import java.time.LocalDate
import javax.`annotation`.processing.Generated
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class WeightDao_Impl(
  __db: RoomDatabase,
) : WeightDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfWeightRecord: EntityInsertAdapter<WeightRecord>

  private val __converters: Converters = Converters()
  init {
    this.__db = __db
    this.__insertAdapterOfWeightRecord = object : EntityInsertAdapter<WeightRecord>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `weight_records` (`id`,`babyId`,`date`,`weightKg`) VALUES (nullif(?, 0),?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: WeightRecord) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.babyId)
        val _tmp: String? = __converters.fromLocalDate(entity.date)
        if (_tmp == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmp)
        }
        statement.bindDouble(4, entity.weightKg)
      }
    }
  }

  public override suspend fun insert(record: WeightRecord): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfWeightRecord.insertAndReturnId(_connection, record)
    _result
  }

  public override fun getForBaby(babyId: Long): Flow<List<WeightRecord>> {
    val _sql: String = "SELECT * FROM weight_records WHERE babyId = ? ORDER BY date ASC, id ASC"
    return createFlow(__db, false, arrayOf("weight_records")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, babyId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfBabyId: Int = getColumnIndexOrThrow(_stmt, "babyId")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfWeightKg: Int = getColumnIndexOrThrow(_stmt, "weightKg")
        val _result: MutableList<WeightRecord> = mutableListOf()
        while (_stmt.step()) {
          val _item: WeightRecord
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
          val _tmpWeightKg: Double
          _tmpWeightKg = _stmt.getDouble(_columnIndexOfWeightKg)
          _item = WeightRecord(_tmpId,_tmpBabyId,_tmpDate,_tmpWeightKg)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getDistinctDates(babyId: Long): Flow<List<LocalDate>> {
    val _sql: String = "SELECT DISTINCT date FROM weight_records WHERE babyId = ?"
    return createFlow(__db, false, arrayOf("weight_records")) { _connection ->
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

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
