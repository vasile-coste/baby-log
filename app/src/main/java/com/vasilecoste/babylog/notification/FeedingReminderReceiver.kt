package com.vasilecoste.babylog.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.vasilecoste.babylog.BabyLogApplication
import com.vasilecoste.babylog.MainActivity
import com.vasilecoste.babylog.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class FeedingReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val babyId = intent.getLongExtra("babyId", -1L)
        if (babyId == -1L) return

        val application = context.applicationContext as BabyLogApplication
        val repository = application.container.repository

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val baby = repository.getBabyById(babyId)
                val babyName = baby?.name ?: ""

                showNotification(context, babyId, babyName)
                vibrate(context)

                // Clear the next reminder time since it just fired
                val pref = repository.notificationPreference(babyId).firstOrNull()
                if (pref != null) {
                    repository.setNotificationPreference(pref.copy(nextReminderEpochMillis = null))
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun showNotification(context: Context, babyId: Long, babyName: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            babyId.toInt(),
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (babyName.isNotEmpty()) {
            context.getString(R.string.feeding_reminder_title) + ": $babyName"
        } else {
            context.getString(R.string.feeding_reminder_title)
        }
        val text = context.getString(R.string.feeding_reminder_text)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.id_feed)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_OFFSET + babyId.toInt(), notification)
    }

    private fun vibrate(context: Context) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (vibrator.hasVibrator()) {
            val pattern = longArrayOf(0, 500, 200, 500)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, -1)
            }
        }
    }

    companion object {
        const val CHANNEL_ID = "feeding_reminders"
        const val NOTIFICATION_ID_OFFSET = 2000

        /**
         * Created eagerly from [BabyLogApplication.onCreate] so the channel exists before the
         * first alarm ever fires, including in the minimal process Android spins up just to
         * deliver the broadcast when the app isn't otherwise running.
         */
        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val name = context.getString(R.string.feeding_reminder_channel_name)
                val channel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH).apply {
                    enableVibration(true)
                }
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}
