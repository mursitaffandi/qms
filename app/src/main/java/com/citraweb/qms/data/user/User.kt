package com.citraweb.qms.data.user

import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName

@Keep
data class User(
        @get:PropertyName("email")
        @set:PropertyName("email")
        var email: String? = null,

        @get:PropertyName("name")
        @set:PropertyName("name")
        var name: String? = null,

        @get:PropertyName("departmentId")
        @set:PropertyName("departmentId")
        var departmentId: String? = null,

        @get:PropertyName("role")
        @set:PropertyName("role")
        var role: Int? = null,

        @get:PropertyName("fcm")
        @set:PropertyName("fcm")
        var fcm: String? = null,

)