package com.citraweb.qms.data.department

import androidx.annotation.Keep

@Keep
data class Department(
    var name : String? = null,
    val clazz : Int? = null
)
