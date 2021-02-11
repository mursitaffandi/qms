package com.citraweb.qms.service

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import com.citraweb.qms.data.Data
import com.citraweb.qms.data.user.UserRepositoryImpl
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import timber.log.Timber


class MyMessagingService : FirebaseMessagingService() {
    private lateinit var viewmodel: MyMessagingViewModel
    companion object {
        var isDashboardForeGround = false
    }

    override fun onCreate() {
        super.onCreate()
        viewmodel = MyMessagingViewModel(this, UserRepositoryImpl())

    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        // Get updated InstanceID token.
        Log.d("onNewToken", "Refreshed token: $p0")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//       TODO : sendRegistrationToServer(refreshedToken)
        viewmodel.sendNewToken(p0)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("onNewMessage", "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            viewmodel.processPayload(remoteMessage)
            Timber.d("Message data payload: %s", remoteMessage.data)
            if ( /* Check if data needs to be processed by long running job */true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//               TODO : scheduleJob()
            } else {
                // Handle message within 10 seconds
//              TODO:  handleNow()
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Timber.d("Message Notification Body: %s", remoteMessage.notification!!.body)

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}