package com.citraweb.qms.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
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

                data.putExtra(
                    KEY_EXTRA_BROADCAST, gson.fromJson(
                        gson.toJson(message.data),
                        Data::class.java
                    )
                )
                if (isDashboardForeGround)
                    localBroadcastManager.sendBroadcast(data)
                else {
                    data.component = ComponentName(ctx, DashboardActivity::class.java)

                    ctx.startActivity(data)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        // only for gingerbread and newer versions
//                        TODO : Display notif and sound
                        displayNotification(data, ctx)
                    }

                }
            }

            /*message.notification?.let {
                //TODO : handle legacy notification FCM  content
            }*/
        }
    }

    private fun displayNotification(intent: Intent, _ctx: Context) {
//        TODO : create notif widget
        val builder = NotificationCompat.Builder(_ctx, "")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Notifications Example")
            .setContentText("This is a test notification")


        val contentIntent = PendingIntent.getActivity(
            _ctx, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        builder.setSound(alarmSound)
        builder.setContentIntent(contentIntent)
        builder.setAutoCancel(true)
        builder.setLights(Color.BLUE, 500, 500)
        val pattern = longArrayOf(500, 500, 500, 500, 500, 500, 500, 500, 500)
        builder.setVibrate(pattern)
        builder.setStyle(NotificationCompat.InboxStyle())
        // Add as notification
        val manager = _ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        manager!!.notify(1, builder.build())
    }
}