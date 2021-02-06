package com.citraweb.qms.service

import android.util.Log
import com.citraweb.qms.data.user.UserRepositoryImpl.Companion.updateFcmToken
import com.citraweb.qms.utils.Result
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber


class MyMessagingService : FirebaseMessagingService() {
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
        GlobalScope.launch {
            when(val up = updateFcmToken(p0)){
                is Result.Success -> {
                    Timber.d("updateFcmToken success")
                }
                is Result.Error -> {Timber.e(up.exception)}
                is Result.Canceled -> {Timber.e(up.exception)}
            }
        }
    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("onNewMessage","From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {

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