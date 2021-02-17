package com.citraweb.qms.data

data class ResultData<out T>(
    val success : T? = null,
    val loading : Boolean? = null,
    val message : Int? = null
)
