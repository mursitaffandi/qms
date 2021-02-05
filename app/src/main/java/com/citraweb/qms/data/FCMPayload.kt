package com.citraweb.qms.data

data class FCMPayload(
    var to: String,
    var data: Data,
    )
data class Data(
    var departmentName: String,
    var companyName: String,
    var priority: String,
)
