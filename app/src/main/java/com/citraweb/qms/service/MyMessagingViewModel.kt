package com.citraweb.qms.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.citraweb.qms.data.Data
import com.citraweb.qms.data.user.UserRepository
import com.citraweb.qms.service.MyMessagingService.Companion.isDashboardForeGround
import com.citraweb.qms.ui.dashboard.DashboardActivity
import com.citraweb.qms.utils.BROADCAST_CALLED_QUEUE
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
            if (message.data.isNotEmpty()) {
                val data = Intent()
                data.action = BROADCAST_CALLED_QUEUE
                data.putExtra(
                    "", gson.fromJson(
                        gson.toJson(message.data),
                        Data::class.java
                    )
                )
                if (isDashboardForeGround)
                    localBroadcastManager.sendBroadcast(data)
                else {
                    data.component = ComponentName(ctx, DashboardActivity::class.java)
                    data.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    data.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    ctx.startActivity(data)
                }
            }

            message.notification?.let {
                displayNotification(it)
            }
        }
    }

    private fun displayNotification(notif: RemoteMessage.Notification) {
//        TODO : create notif widget
    }
}