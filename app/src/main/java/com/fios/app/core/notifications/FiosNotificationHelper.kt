package com.fios.app.core.notifications
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
class FiosNotificationHelper(private val context: Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    companion object {
        const val CHANNEL_ID = "fios_system_alerts"
        const val CHANNEL_NAME = "Alertas del Sistema FIOS"
    }
    init {
        createNotificationChannel()
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de logs automáticos y alertas de sistema"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
    fun sendSystemNotification(title: String, message: String, notificationId: Int = System.currentTimeMillis().toInt()) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        notificationManager.notify(notificationId, builder.build())
    }
}