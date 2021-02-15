package com.citraweb.qms.data.queue

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

@Keep
class Queue(
        @get:PropertyName("createdAt")
        @set:PropertyName("createdAt")
        var createdAt: Timestamp? = Timestamp.now(),

        @get:PropertyName("updatedAt")
        @set:PropertyName("updatedAt")
        var updatedAt: Timestamp? = Timestamp.now(),


        @get:PropertyName("department")
        @set:PropertyName("department")
        var department: String? = null,


        @get:PropertyName("status")
        @set:PropertyName("status")
        var status: String? = null,


        @get:PropertyName("prefix")
        @set:PropertyName("prefix")
        var prefix: String? = null,

        @get:PropertyName("ticket")
        @set:PropertyName("ticket")
        var ticket: Int? = null,


        @get:PropertyName("user")
        @set:PropertyName("user")
        var user: String? = null,

        @get:PropertyName("caller")
        @set:PropertyName("caller")
        var caller: String? = null,


        @get:PropertyName("waiting")
        @set:PropertyName("waiting")
        var waiting: Int? = 0,
        )