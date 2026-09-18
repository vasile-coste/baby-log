package com.vasilecoste.babylog.ui.tummytime

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.vasilecoste.babylog.BabyLogApplication
import com.vasilecoste.babylog.MainActivity
import com.vasilecoste.babylog.R
import com.vasilecoste.babylog.data.repository.TummyTimerStart
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class TummyTimeService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = Runnable { updateNotification() }

    private val repository by lazy { (application as BabyLogApplication).container.repository }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val babyId = intent.getLongExtra(EXTRA_BABY_ID, -1L)
                val date = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getSerializableExtra(EXTRA_DATE, LocalDate::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getSerializableExtra(EXTRA_DATE) as? LocalDate
                }
                val startTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getSerializableExtra(EXTRA_START_TIME, LocalTime::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getSerializableExtra(EXTRA_START_TIME) as? LocalTime
                }
                val epochMillis = intent.getLongExtra(EXTRA_EPOCH_MILLIS, 0L)

                if (babyId != -1L && date != null && startTime != null) {
                    val timerStart = TummyTimerStart(babyId, date, startTime, epochMillis)
                    repository.runningTummyTimer.value = timerStart
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        startForeground(
                            NOTIFICATION_ID,
                            createNotification(timerStart),
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE else 0
                        )
                    } else {
                        startForeground(NOTIFICATION_ID, createNotification(timerStart))
                    }
                    handler.post(updateRunnable)
                }
            }
            ACTION_STOP -> {
                stopTimer()
            }
        }
        return START_NOT_STICKY
    }

    private fun stopTimer() {
        val timerStart = repository.runningTummyTimer.value
        if (timerStart != null) {
            val elapsedSeconds = ((System.currentTimeMillis() - timerStart.epochMillis) / 1000).toInt().coerceAtLeast(0)
            if (elapsedSeconds > 0) {
                serviceScope.launch {
                    repository.addTummyTimeEntry(
                        timerStart.babyId,
                        timerStart.date,
                        timerStart.startTime,
                        elapsedSeconds
                    )
                    repository.runningTummyTimer.value = null
                    stopSelf()
                }
            } else {
                repository.runningTummyTimer.value = null
                stopSelf()
            }
        } else {
            stopSelf()
        }
    }

    private fun updateNotification() {
        val timerStart = repository.runningTummyTimer.value
        if (timerStart != null) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, createNotification(timerStart))
            handler.postDelayed(updateRunnable, 1000)
        }
    }

    private fun createNotification(timerStart: TummyTimerStart): Notification {
        createNotificationChannel()

        val elapsedSeconds = ((System.currentTimeMillis() - timerStart.epochMillis) / 1000).toInt().coerceAtLeast(0)
        val timeText = "%02d:%02d".format(elapsedSeconds / 60, elapsedSeconds % 60)

        val mainIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_SCREEN, "TUMMY_TIME")
        }
        val mainPendingIntent = PendingIntent.getActivity(
            this, System.currentTimeMillis().toInt(), mainIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopIntent = Intent(this, TummyTimeService::class.java).apply { action = ACTION_STOP }
        val stopPendingIntent = PendingIntent.getService(
            this, 1, stopIntent, PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.id_timer) // Fallback to sleep icon as it looks like a timer/clock sometimes, or use a better one if available
            .setContentTitle(getString(R.string.tummy_time_notification_title))
            .setContentText(getString(R.string.tummy_time_notification_text, timeText))
            .setOngoing(true)
            .setContentIntent(mainPendingIntent)
            .addAction(
                R.drawable.id_timer,
                getString(R.string.tummy_time_notification_action_stop),
                stopPendingIntent
            )
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.tummy_time_notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        handler.removeCallbacks(updateRunnable)
        serviceScope.cancel()
        super.onDestroy()
    }

    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "tummy_time_timer"
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val EXTRA_BABY_ID = "EXTRA_BABY_ID"
        const val EXTRA_DATE = "EXTRA_DATE"
        const val EXTRA_START_TIME = "EXTRA_START_TIME"
        const val EXTRA_EPOCH_MILLIS = "EXTRA_EPOCH_MILLIS"
        const val EXTRA_SCREEN = "EXTRA_SCREEN"
    }
}
