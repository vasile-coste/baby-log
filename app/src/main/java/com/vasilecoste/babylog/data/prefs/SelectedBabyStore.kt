package com.vasilecoste.babylog.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "baby_log_prefs")

class SelectedBabyStore(private val context: Context) {
    private val selectedBabyIdKey = longPreferencesKey("selected_baby_id")

    val selectedBabyId: Flow<Long?> = context.dataStore.data.map { prefs ->
        prefs[selectedBabyIdKey]
    }

    suspend fun setSelectedBabyId(babyId: Long) {
        context.dataStore.edit { prefs -> prefs[selectedBabyIdKey] = babyId }
    }
}
