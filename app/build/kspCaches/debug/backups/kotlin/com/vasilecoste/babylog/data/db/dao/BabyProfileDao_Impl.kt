package com.vasilecoste.babylog.`data`.db.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.vasilecoste.babylog.`data`.db.Converters
import com.vasilecoste.babylog.`data`.db.entity.BabyProfile
import java.time.LocalDate
import javax.`annotation`.processing.Generated
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
public class BabyProfileDao_Impl(
  __db: RoomDatabase,
) : BabyProfileDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfBabyProfile: EntityInsertAdapter<BabyProfile>

  private val __converters: Converters = Converters()
  init {
    this.__db = __db
    this.__insertAdapterOfBabyProfile = object : EntityInsertAdapter<BabyProfile>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `baby_profiles` (`id`,`name`,`birthDate`,`createdAtEpochMillis`) VALUES (nullif(?, 0),?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: BabyProfile) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.name)
        val _tmpBirthDate: LocalDate? = entity.birthDate
        val _tmp: String? = __converters.fromLocalDate(_tmpBirthDate)
        if (_tmp == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmp)
        }
        statement.bindLong(4, entity.createdAtEpochMillis)
      }
    }
  }

  public override suspend fun insert(profile: BabyProfile): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfBabyProfile.insertAndReturnId(_connection, profile)
    _result
  }

  public override fun getAll(): Flow<List<BabyProfile>> {
    val _sql: String = "SELECT * FROM baby_profiles ORDER BY createdAtEpochMillis ASC"
    return createFlow(__db, false, arrayOf("baby_profiles")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfCreatedAtEpochMillis: Int = getColumnIndexOrThrow(_stmt, "createdAtEpochMillis")
        val _result: MutableList<BabyProfile> = mutableListOf()
        while (_stmt.step()) {
          val _item: BabyProfile
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpBirthDate: LocalDate?
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfBirthDate)
          }
          _tmpBirthDate = __converters.toLocalDate(_tmp)
          val _tmpCreatedAtEpochMillis: Long
          _tmpCreatedAtEpochMillis = _stmt.getLong(_columnIndexOfCreatedAtEpochMillis)
          _item = BabyProfile(_tmpId,_tmpName,_tmpBirthDate,_tmpCreatedAtEpochMillis)
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
