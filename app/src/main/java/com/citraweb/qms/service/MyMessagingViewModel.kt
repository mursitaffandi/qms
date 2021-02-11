package com.citraweb.qms.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.citraweb.qms.R
import com.citraweb.qms.data.Data
import com.citraweb.qms.data.user.UserRepository
import com.citraweb.qms.service.MyMessagingService.Companion.isDashboardForeGround
import com.citraweb.qms.ui.dashboard.DashboardActivity
import com.citraweb.qms.utils.ACTION_BROADCAST_CALLED_QUEUE
import com.citraweb.qms.utils.KEY_EXTRA_BROADCAST
import com.citraweb.qms.utils.Result
import com.citraweb.qms.utils.SharePrefManager
import com.citraweb.qms.utils.SharePrefManager.Companion.ID_USER
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber


class MyMessagingViewModel(private val ctx: Context, private val repository: UserRepository) {
    private val prefmanager = SharePrefManager(ctx)
    private val gson = Gson()
    var localBroadcastManager = LocalBroadcastManager.getInstance(ctx)

    // declaring variables
    var notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "com.citraweb.qms.service"
    private val description = "queue notification"

    fun sendNewToken(newToken: String) {
        GlobalScope.launch {
            when (val up = repository.updateFcmToken(newToken)) {
                is Result.Success -> {
                    Timber.d("updateFcmToken success")
                }
                is Result.Error -> {
                    Timber.e(up.exception)
                }
                is Result.Canceled -> {
                    Timber.e(up.exception)
                }
            }
        }
    }

    fun processPayload(message: RemoteMessage) {
        if (prefmanager.getFromPreference(ID_USER).isNotEmpty()) {
            val data = Intent()
            data.action = ACTION_BROADCAST_CALLED_QUEUE
            data.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            if (message.data.isNotEmpty()) {
                val fcmData = gson.fromJson(
                        gson.toJson(message.data),
                        Data::class.java)

                data.putExtra(KEY_EXTRA_BROADCAST, fcmData)
                if (isDashboardForeGround)
                    localBroadcastManager.sendBroadcast(data)
                else {
                    data.component = ComponentName(ctx, DashboardActivity::class.java)
                    ctx.startActivity(data)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        // only for gingerbread and newer versions
//                        TODO : Display notif and sound
                        displayNotification(data, ctx, fcmData.companyName, fcmData.departmentName)
                    }

                }
            }

            /*message.notification?.let {
                //TODO : handle legacy notification FCM  content
            }*/
        }
    }

    private fun displayNotification(intent: Intent, _ctx: Context, notif_title: String, notif_content: String) {
        val pendingIntent = PendingIntent.getActivity(_ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationChannel.setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                            .build()
            )

            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(_ctx, channelId)
                    .setContentTitle(notif_title)
                    .setContentText(notif_content)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(_ctx.resources, R.drawable.ic_launcher_background))
                    .setContentIntent(pendingIntent)
        } else {
            builder = Notification.Builder(_ctx)
                    .setVibrate(longArrayOf(0, 500, 1000))
                    .setSound(
                            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                            AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                                    .build()
                    )
                    .setContentTitle(notif_title)
                    .setContentText(notif_content)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(_ctx.resources, R.drawable.ic_launcher_background))
                    .setContentIntent(pendingIntent)
        }
        notificationManager.notify(1234, builder.build())
    }
}