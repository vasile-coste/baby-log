package com.vasilecoste.babylog.ui.importexport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vasilecoste.babylog.BabyLogApplication
import com.vasilecoste.babylog.data.db.entity.BabyProfile
import com.vasilecoste.babylog.data.json.BabyDataJson
import com.vasilecoste.babylog.data.model.ImportedBabyData
import com.vasilecoste.babylog.data.repository.BabyLogRepository
import com.vasilecoste.babylog.data.repository.ImportMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ImportExportViewModel(private val repository: BabyLogRepository) : ViewModel() {

    val babies: StateFlow<List<BabyProfile>> =
        repository.babies.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun parseImport(jsonText: String): Result<ImportedBabyData> = try {
        Result.success(BabyDataJson.parse(jsonText))
    } catch (t: Throwable) {
        Result.failure(t)
    }

    suspend fun importData(
        data: ImportedBabyData,
        existingBabyId: Long? = null,
        mode: ImportMode = ImportMode.MERGE,
    ): Result<Pair<String, Int>> = try {
        repository.importBabyData(data, existingBabyId, mode)
        Result.success(data.babyName to data.entries.size)
    } catch (t: Throwable) {
        Result.failure(t)
    }

    suspend fun exportJson(babyId: Long): Result<String> = try {
        val data = repository.exportBabyData(babyId)
        if (data == null) {
            Result.failure(IllegalStateException("Baby not found"))
        } else {
            Result.success(BabyDataJson.serialize(data))
        }
    } catch (t: Throwable) {
        Result.failure(t)
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as BabyLogApplication
                ImportExportViewModel(app.container.repository)
            }
        }
    }
}
