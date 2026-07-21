package com.vasilecoste.babylog.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vasilecoste.babylog.BabyLogApplication
import com.vasilecoste.babylog.data.db.entity.BabyProfile
import com.vasilecoste.babylog.data.db.entity.Entry
import com.vasilecoste.babylog.data.prefs.SelectedBabyStore
import com.vasilecoste.babylog.data.repository.BabyLogRepository
import com.vasilecoste.babylog.data.repository.DailyAggregate
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private data class ProfilesState(val babies: List<BabyProfile>, val selectedBaby: BabyProfile?)

private data class DayState(
    val selectedDate: LocalDate,
    val entries: List<Entry>,
    val pickerDates: List<LocalDate>,
    val chartData: List<DailyAggregate>,
)

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val repository: BabyLogRepository,
    private val selectedBabyStore: SelectedBabyStore,
) : ViewModel() {

    private val selectedDate = MutableStateFlow(LocalDate.now())

    private val selectedBabyId: StateFlow<Long?> =
        combine(repository.babies, selectedBabyStore.selectedBabyId) { babies, stored ->
            stored?.takeIf { id -> babies.any { it.id == id } } ?: babies.firstOrNull()?.id
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val profilesState: Flow<ProfilesState> =
        combine(repository.babies, selectedBabyId) { babies, selectedId ->
            ProfilesState(babies, babies.find { it.id == selectedId })
        }

    private val entries = combine(selectedBabyId, selectedDate) { id, date -> id to date }
        .flatMapLatest { (id, date) ->
            if (id == null) flowOf(emptyList()) else repository.entriesForDay(id, date)
        }

    private val pickerDates = selectedBabyId.flatMapLatest { id ->
        if (id == null) {
            flowOf(listOf(LocalDate.now()))
        } else {
            repository.datesWithData(id)
        }
    }

    private val chartData = selectedBabyId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.dailyChartData(id)
    }

    private val dayState: Flow<DayState> =
        combine(selectedDate, entries, pickerDates, chartData) { date, entries, pickerDates, chart ->
            val allPickerDates = (pickerDates + LocalDate.now()).distinct().sortedDescending()
            DayState(date, entries, allPickerDates, chart)
        }

    val uiState: StateFlow<MainUiState> = combine(profilesState, dayState) { profiles, day ->
        MainUiState(
            babies = profiles.babies,
            selectedBaby = profiles.selectedBaby,
            selectedDate = day.selectedDate,
            entries = day.entries,
            pickerDates = day.pickerDates,
            chartData = day.chartData,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MainUiState())

    fun selectDate(date: LocalDate) {
        selectedDate.value = date
    }

    fun selectBaby(babyId: Long) {
        viewModelScope.launch { selectedBabyStore.setSelectedBabyId(babyId) }
    }

    fun addBabyProfile(name: String) {
        viewModelScope.launch {
            val id = repository.addBabyProfile(name)
            selectedBabyStore.setSelectedBabyId(id)
        }
    }

    fun addEntry(time: LocalTime, foodMl: Int?, poop: Boolean, pee: Boolean, vitamin: Boolean) {
        val babyId = selectedBabyId.value ?: return
        viewModelScope.launch {
            repository.addEntry(babyId, selectedDate.value, time, foodMl, poop, pee, vitamin)
        }
    }

    fun updateEntry(entry: Entry) {
        viewModelScope.launch { repository.updateEntry(entry) }
    }

    fun deleteEntry(entry: Entry) {
        viewModelScope.launch { repository.deleteEntry(entry) }
    }

    fun addWeight(weightKg: Double) {
        val babyId = selectedBabyId.value ?: return
        viewModelScope.launch {
            repository.addWeight(babyId, selectedDate.value, weightKg)
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as BabyLogApplication
                MainViewModel(app.container.repository, app.container.selectedBabyStore)
            }
        }
    }
}
