package com.vasilecoste.babylog.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vasilecoste.babylog.BabyLogApplication
import com.vasilecoste.babylog.data.db.entity.BabyProfile
import com.vasilecoste.babylog.data.db.entity.DiaperSummary
import com.vasilecoste.babylog.data.db.entity.Entry
import com.vasilecoste.babylog.data.db.entity.SleepEntry
import com.vasilecoste.babylog.data.db.entity.TummyTimeEntry
import com.vasilecoste.babylog.data.db.entity.WeightRecord
import com.vasilecoste.babylog.data.prefs.SelectedBabyStore
import com.vasilecoste.babylog.data.repository.BabyLogRepository
import com.vasilecoste.babylog.data.repository.DailyAggregate
import com.vasilecoste.babylog.ui.theme.AppTheme
import com.vasilecoste.babylog.ui.theme.resolveAppTheme
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private data class ProfilesState(val babies: List<BabyProfile>, val selectedBaby: BabyProfile?)

private data class DayState(
    val selectedDate: LocalDate,
    val entries: List<Entry>,
    val diaperSummary: DiaperSummary?,
    val pickerDates: List<LocalDate>,
    val chartData: List<DailyAggregate>,
    val tummyTimeEntries: List<TummyTimeEntry>,
    val sleepEntries: List<SleepEntry>,
)

private data class DaySecondaryState(
    val pickerDates: List<LocalDate>,
    val chartData: List<DailyAggregate>,
    val tummyTimeEntries: List<TummyTimeEntry>,
    val sleepEntries: List<SleepEntry>,
)

private data class TummyTimerStart(val epochMillis: Long, val date: LocalDate, val time: LocalTime)

private data class ThemeState(val override: AppTheme?, val active: AppTheme)

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val repository: BabyLogRepository,
    private val selectedBabyStore: SelectedBabyStore,
) : ViewModel() {

    private val selectedDate = MutableStateFlow(LocalDate.now())
    private val selectedMonth = MutableStateFlow(YearMonth.now())

    private val selectedBabyId: StateFlow<Long?> =
        combine(repository.babies, selectedBabyStore.selectedBabyId) { babies, stored ->
            stored?.takeIf { id -> babies.any { it.id == id } } ?: babies.firstOrNull()?.id
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val profilesState: Flow<ProfilesState> =
        combine(repository.babies, selectedBabyId) { babies, selectedId ->
            ProfilesState(babies, babies.find { it.id == selectedId })
        }

    private val themeOverride: Flow<AppTheme?> = selectedBabyId.flatMapLatest { id ->
        if (id == null) flowOf(null) else repository.themeOverride(id).map { raw ->
            raw?.let { runCatching { AppTheme.valueOf(it) }.getOrNull() }
        }
    }

    private val themeState: Flow<ThemeState> = combine(profilesState, themeOverride) { profiles, override ->
        ThemeState(override, resolveAppTheme(profiles.selectedBaby?.gender, override))
    }

    private val entries = combine(selectedBabyId, selectedDate) { id, date -> id to date }
        .flatMapLatest { (id, date) ->
            if (id == null) flowOf(emptyList()) else repository.entriesForDay(id, date)
        }

    private val diaperSummary = combine(selectedBabyId, selectedDate) { id, date -> id to date }
        .flatMapLatest { (id, date) ->
            if (id == null) flowOf<DiaperSummary?>(null) else repository.diaperSummaryForDay(id, date)
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

    private val tummyTimeEntries = combine(selectedBabyId, selectedDate) { id, date -> id to date }
        .flatMapLatest { (id, date) ->
            if (id == null) flowOf(emptyList()) else repository.tummyTimeForDay(id, date)
        }

    private val tummyTimerStart = MutableStateFlow<TummyTimerStart?>(null)

    private val sleepEntries = combine(selectedBabyId, selectedDate) { id, date -> id to date }
        .flatMapLatest { (id, date) ->
            if (id == null) flowOf(emptyList()) else repository.sleepForDay(id, date)
        }

    private val weightRecords = selectedBabyId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.weightsForBaby(id)
    }

    private val dayState: Flow<DayState> =
        combine(
            combine(selectedDate, entries, diaperSummary) { date, entries, summary -> Triple(date, entries, summary) },
            combine(pickerDates, chartData, tummyTimeEntries, sleepEntries) { picker, chart, tummy, sleep ->
                DaySecondaryState(picker, chart, tummy, sleep)
            },
        ) { (date, entries, summary), secondary ->
            val allPickerDates = (secondary.pickerDates + LocalDate.now()).distinct().sortedDescending()
            DayState(date, entries, summary, allPickerDates, secondary.chartData, secondary.tummyTimeEntries, secondary.sleepEntries)
        }

    val uiState: StateFlow<MainUiState> =
        combine(
            combine(profilesState, dayState, tummyTimerStart) { profiles, day, timer -> Triple(profiles, day, timer) },
            combine(themeState, weightRecords, selectedMonth) { theme, weights, month -> Triple(theme, weights, month) }
        ) { (profiles, day, timer), (theme, weights, month) ->
            MainUiState(
                babies = profiles.babies,
                selectedBaby = profiles.selectedBaby,
                selectedDate = day.selectedDate,
                entries = day.entries,
                diaperSummary = day.diaperSummary,
                pickerDates = day.pickerDates,
                chartData = day.chartData,
                tummyTimeEntries = day.tummyTimeEntries,
                tummyTimerRunning = timer != null,
                tummyTimerStartEpochMillis = timer?.epochMillis,
                sleepEntries = day.sleepEntries,
                activeTheme = theme.active,
                themeOverride = theme.override,
                weightRecords = weights,
                selectedMonth = month,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MainUiState())

    fun selectDate(date: LocalDate) {
        selectedDate.value = date
    }

    fun selectMonth(month: YearMonth) {
        selectedMonth.value = month
    }

    fun selectBaby(babyId: Long) {
        viewModelScope.launch { selectedBabyStore.setSelectedBabyId(babyId) }
    }

    fun addBabyProfile(name: String, birthDate: LocalDate? = null, gender: String? = null) {
        viewModelScope.launch {
            val id = repository.addBabyProfile(name, birthDate, gender)
            selectedBabyStore.setSelectedBabyId(id)
        }
    }

    fun setThemeOverride(babyId: Long, theme: AppTheme?) {
        viewModelScope.launch { repository.setThemeOverride(babyId, theme?.name) }
    }

    fun updateBabyProfile(id: Long, name: String, birthDate: LocalDate?, gender: String?) {
        viewModelScope.launch {
            val existing = repository.getBabyById(id) ?: return@launch
            repository.updateBabyProfile(
                existing.copy(
                    name = name,
                    birthDate = birthDate,
                    gender = gender
                )
            )
        }
    }

    fun deleteBabyProfile(baby: BabyProfile) {
        viewModelScope.launch { repository.deleteBabyProfile(baby) }
    }

    fun addEntry(time: LocalTime, foodMl: Int?, poop: Boolean, pee: Boolean, puke: Boolean, vitamin: Boolean, breastfed: Boolean) {
        val babyId = selectedBabyId.value ?: return
        viewModelScope.launch {
            repository.addEntry(babyId, selectedDate.value, time, foodMl, poop, pee, puke, vitamin, breastfed)
        }
    }

    fun updateEntry(entry: Entry) {
        viewModelScope.launch { repository.updateEntry(entry) }
    }

    fun deleteEntry(entry: Entry) {
        viewModelScope.launch { repository.deleteEntry(entry) }
    }

    fun addWeight(weightKg: Double?, heightCm: Double?) {
        val babyId = selectedBabyId.value ?: return
        viewModelScope.launch {
            repository.addWeight(babyId, selectedDate.value, weightKg, heightCm)
        }
    }

    fun addWeightRecord(date: LocalDate, weightKg: Double?, heightCm: Double?) {
        val babyId = selectedBabyId.value ?: return
        viewModelScope.launch {
            repository.addWeight(babyId, date, weightKg, heightCm)
        }
    }

    fun updateWeightRecord(record: WeightRecord) {
        viewModelScope.launch { repository.updateWeightRecord(record) }
    }

    fun deleteWeightRecord(record: WeightRecord) {
        viewModelScope.launch { repository.deleteWeightRecord(record) }
    }

    fun startTummyTimer() {
        if (tummyTimerStart.value != null) return
        tummyTimerStart.value = TummyTimerStart(System.currentTimeMillis(), LocalDate.now(), LocalTime.now())
    }

    fun stopTummyTimer() {
        val start = tummyTimerStart.value ?: return
        tummyTimerStart.value = null
        val babyId = selectedBabyId.value ?: return
        val elapsedSeconds = ((System.currentTimeMillis() - start.epochMillis) / 1000).toInt().coerceAtLeast(0)
        if (elapsedSeconds <= 0) return
        viewModelScope.launch {
            repository.addTummyTimeEntry(babyId, start.date, start.time, elapsedSeconds)
        }
    }

    fun addManualTummyTime(date: LocalDate, startTime: LocalTime, durationSeconds: Int) {
        val babyId = selectedBabyId.value ?: return
        viewModelScope.launch {
            repository.addTummyTimeEntry(babyId, date, startTime, durationSeconds)
        }
    }

    fun updateTummyTimeEntry(entry: TummyTimeEntry) {
        viewModelScope.launch { repository.updateTummyTimeEntry(entry) }
    }

    fun deleteTummyTimeEntry(entry: TummyTimeEntry) {
        viewModelScope.launch { repository.deleteTummyTimeEntry(entry) }
    }

    fun addManualSleep(date: LocalDate, startTime: LocalTime, endTime: LocalTime?) {
        val babyId = selectedBabyId.value ?: return
        viewModelScope.launch {
            repository.addSleepEntry(babyId, date, startTime, endTime)
        }
    }

    fun updateSleepEntry(entry: SleepEntry) {
        viewModelScope.launch { repository.updateSleepEntry(entry) }
    }

    fun deleteSleepEntry(entry: SleepEntry) {
        viewModelScope.launch { repository.deleteSleepEntry(entry) }
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
