package com.citraweb.qms.data

data class ResultData<out T>(
    val success : T? = null,
    val message : Int
)
