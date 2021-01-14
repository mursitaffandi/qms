package com.citraweb.qms.data

data class ResultData<T>(
    val success : T? = null,
    val message : Int
)
