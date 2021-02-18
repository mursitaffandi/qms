package com.citraweb.qms.service

import android.util.Log
import com.citraweb.qms.data.user.UserRepositoryImpl
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
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
        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Timber.d("Message Notification Body: %s", remoteMessage.notification!!.body)

        }
    }
}