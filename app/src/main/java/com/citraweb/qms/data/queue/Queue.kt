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
        var department: String? = "",

        @get:PropertyName("departmentName")
        @set:PropertyName("departmentName")
        var departmentName: String? = "",

        @get:PropertyName("departmentCompany")
        @set:PropertyName("departmentCompany")
        var departmentCompany: String? = "",


        @get:PropertyName("status")
        @set:PropertyName("status")
        var status: String? = "",


        @get:PropertyName("prefix")
        @set:PropertyName("prefix")
        var prefix: String? = "",

        @get:PropertyName("ticket")
        @set:PropertyName("ticket")
        var ticket: Int? = 0,


        @get:PropertyName("user")
        @set:PropertyName("user")
        var user: String? = "",

        @get:PropertyName("caller")
        @set:PropertyName("caller")
        var caller: String? = "",


        @get:PropertyName("waiting")
        @set:PropertyName("waiting")
        var waiting: Int? = 0,
        )