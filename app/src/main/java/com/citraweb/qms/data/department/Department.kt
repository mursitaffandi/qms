package com.citraweb.qms.data.department

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

@Keep
data class Department(
        @get:PropertyName("companyId")
        @set:PropertyName("companyId")
        var companyId: String? = null,

        @get:PropertyName("departmentId")
        @set:PropertyName("departmentId")
        var departmentId: String? = null,

        @get:PropertyName("createdAt")
        @set:PropertyName("createdAt")
        var createdAt: Timestamp? = null,


        @get:PropertyName("updatedAt")
        @set:PropertyName("updatedAt")
        var updatedAt: Timestamp? = null,


        @get:PropertyName("name")
        @set:PropertyName("name")
        var name: String? = null,


        @get:PropertyName("prefix")
        @set:PropertyName("prefix")
        var prefix: String? = null,

        @get:PropertyName("staffId")
        @set:PropertyName("staffId")
        var staffId: String? = null,


        @get:PropertyName("status")
        @set:PropertyName("status")
        var status: Int? = null,


        @get:PropertyName("waitings")
        @set:PropertyName("waitings")
        var waitings: HashMap<Int, String>? = null,


        @get:PropertyName("currentQueue")
        @set:PropertyName("currentQueue")
        var currentQueue: Int? = null,


        @get:PropertyName("amount")
        @set:PropertyName("amount")
        var amount: Int? = null

)

