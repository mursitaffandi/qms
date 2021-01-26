package com.citraweb.qms.data.user

import androidx.annotation.Keep
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.PropertyName

@Keep
data class User(
        @get:PropertyName("userId")
        @set:PropertyName("userId")
        var userId: String? = null,

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

        @get:PropertyName("ticket")
        @set:PropertyName("ticket")
        var ticket: Int? = null,

        @get:PropertyName("ticketParent")
        @set:PropertyName("ticketParent")
        var ticketParent: String? = null,
) {
    constructor(
            firebaseUser: FirebaseUser,
            name: String,
    ) : this(
            userId = firebaseUser.uid,
            email = firebaseUser.email,
            name = name,
            departmentId = "",
            role = 2,
            fcm = "",
            ticket = 2,
            ticketParent = "",
            )
}