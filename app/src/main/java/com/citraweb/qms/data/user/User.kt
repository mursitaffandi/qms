package com.citraweb.qms.data.user

import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName

@Keep
data class User(
        @get:PropertyName("email")
        @set:PropertyName("email")
        var email: String? = "",

        @get:PropertyName("name")
        @set:PropertyName("name")
        var name: String? = "",

        @get:PropertyName("departmentId")
        @set:PropertyName("departmentId")
        var departmentId: String? = "",

        @get:PropertyName("role")
        @set:PropertyName("role")
        var role: Int? = 0,

        @get:PropertyName("fcm")
        @set:PropertyName("fcm")
        var fcm: String? = "",

)