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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ImportExportViewModel(private val repository: BabyLogRepository) : ViewModel() {

    val babies: StateFlow<List<BabyProfile>> =
        repository.babies.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _importResult = MutableSharedFlow<Result<Pair<String, Int>>>(extraBufferCapacity = 1)

    // Import runs on viewModelScope (not the screen's coroutine scope) so navigating away mid-import
    // - e.g. right after the theme flips on the new baby's gender - can't cancel it partway through.
    val importResult: SharedFlow<Result<Pair<String, Int>>> = _importResult.asSharedFlow()

    fun parseImport(jsonText: String): Result<ImportedBabyData> = try {
        Result.success(BabyDataJson.parse(jsonText))
    } catch (t: Throwable) {
        Result.failure(t)
    }

    fun importData(
        data: ImportedBabyData,
        existingBabyId: Long? = null,
        mode: ImportMode = ImportMode.MERGE,
    ) {
        viewModelScope.launch {
            val result = try {
                repository.importBabyData(data, existingBabyId, mode)
                Result.success(data.babyName to data.entries.size)
            } catch (t: Throwable) {
                Result.failure(t)
            }
            _importResult.emit(result)
        }
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
