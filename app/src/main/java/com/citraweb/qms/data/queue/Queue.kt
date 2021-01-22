package com.citraweb.qms.data.queue

import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName

@Keep
class Queue(
        @get:PropertyName("name")
        @set:PropertyName("name")
        var name: String? = null,

        @get:PropertyName("company_id")
        @set:PropertyName("company_id")
        var companyId: String? = null,

        @get:PropertyName("created_at")
        @set:PropertyName("created_at")
        var createdAt: String? = null,

        @get:PropertyName("current_queue")
        @set:PropertyName("current_queue")
        var currentQueue: Int? = null,

        @get:PropertyName("is_open")
        @set:PropertyName("is_open")
        var isOpen: Boolean? = null,

        @get:PropertyName("prefix")
        @set:PropertyName("prefix")
        var prefix: String? = null,

        @get:PropertyName("waiting")
        @set:PropertyName("waiting")
        var waiting: Int? = null
)