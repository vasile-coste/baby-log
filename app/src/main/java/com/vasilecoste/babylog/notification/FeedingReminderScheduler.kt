package com.vasilecoste.babylog.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.vasilecoste.babylog.data.repository.BabyLogRepository
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import kotlinx.coroutines.flow.firstOrNull

class FeedingReminderScheduler(
    private val context: Context,
    private val repository: BabyLogRepository
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    suspend fun scheduleNextReminder(babyId: Long) {
        val pref = repository.notificationPreference(babyId).firstOrNull() ?: return
        if (!pref.enabled) {
            cancelReminder(babyId)
            if (pref.nextReminderEpochMillis != null) {
                repository.setNotificationPreference(pref.copy(nextReminderEpochMillis = null))
            }
            return
        }

        val today = LocalDate.now()
        val allDates = repository.datesWithData(babyId).firstOrNull() ?: emptyList()
        
        // Find the most recent feed
        var lastFeedDateTime: LocalDateTime? = null
        for (date in allDates.filter { it <= today }) {
            val entries = repository.entriesForDay(babyId, date).firstOrNull() ?: continue
            val lastFeedToday = entries
                .filter { (it.foodMl ?: 0) > 0 }
                .maxByOrNull { it.time }
            if (lastFeedToday != null) {
                lastFeedDateTime = LocalDateTime.of(date, lastFeedToday.time)
                break
            }
        }

        if (lastFeedDateTime == null) {
            Log.d("FeedingReminder", "No feeds found for baby $babyId, cannot schedule reminder.")
            if (pref.nextReminderEpochMillis != null) {
                repository.setNotificationPreference(pref.copy(nextReminderEpochMillis = null))
            }
            return
        }

        val avgIntervalMinutes = calculateAverageInterval(babyId, pref.avgDays)
        if (avgIntervalMinutes <= 0L) {
            Log.d("FeedingReminder", "Insufficient data to calculate average interval for baby $babyId.")
            if (pref.nextReminderEpochMillis != null) {
                repository.setNotificationPreference(pref.copy(nextReminderEpochMillis = null))
            }
            return
        }

        val reminderTime = lastFeedDateTime.plusMinutes(avgIntervalMinutes - pref.offsetMinutes)
        val now = LocalDateTime.now()

        if (reminderTime.isAfter(now)) {
            val triggerAtMillis = reminderTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val intent = Intent(context, FeedingReminderReceiver::class.java).apply {
                putExtra("babyId", babyId)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                babyId.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                    Log.w("FeedingReminder", "Cannot schedule exact alarms, falling back to inexact.")
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                } else {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                }
                Log.d("FeedingReminder", "Scheduled reminder for baby $babyId at $reminderTime (avg: $avgIntervalMinutes, offset: ${pref.offsetMinutes})")
                
                // Update the next reminder time in DB
                repository.setNotificationPreference(pref.copy(nextReminderEpochMillis = triggerAtMillis))
            } catch (e: Exception) {
                Log.e("FeedingReminder", "Failed to schedule alarm", e)
            }
        } else {
            Log.d("FeedingReminder", "Reminder time $reminderTime for baby $babyId is in the past, not scheduling.")
            if (pref.nextReminderEpochMillis != null) {
                repository.setNotificationPreference(pref.copy(nextReminderEpochMillis = null))
            }
        }
    }

    private fun cancelReminder(babyId: Long) {
        val intent = Intent(context, FeedingReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            babyId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            Log.d("FeedingReminder", "Cancelled reminder for baby $babyId")
        }
    }

    private suspend fun calculateAverageInterval(babyId: Long, daysToLookBack: Int): Long {
        val today = LocalDate.now()
        val startDate = today.minusDays(daysToLookBack.toLong())
        
        val allDatesWithData = repository.datesWithData(babyId).firstOrNull() ?: emptyList()
        val relevantDates = allDatesWithData.filter { it >= startDate && it < today }.sorted()
        
        val allFeeds = mutableListOf<LocalDateTime>()
        for (date in relevantDates) {
            val entries = repository.entriesForDay(babyId, date).firstOrNull() ?: continue
            val dailyFeeds = entries
                .filter { (it.foodMl ?: 0) > 0 }
                .map { LocalDateTime.of(date, it.time) }
            allFeeds.addAll(dailyFeeds)
        }
        
        allFeeds.sort()
        
        if (allFeeds.size < 2) return 0L
        
        val intervals = mutableListOf<Long>()
        for (i in 0 until allFeeds.size - 1) {
            val diff = Duration.between(allFeeds[i], allFeeds[i+1]).toMinutes()
            // Ignore gaps > 12 hours (overnight/missed) to get a more accurate 'active' feeding cycle
            // and ignore 0 minute diffs (double entries)
            if (diff in 1..720) { 
                intervals.add(diff)
            }
        }
        
        val avg = if (intervals.isNotEmpty()) intervals.average().toLong() else 0L
        Log.d("FeedingReminder", "Calculated avg interval for baby $babyId over $daysToLookBack days: $avg mins (from ${intervals.size} intervals)")
        return avg
    }
}
