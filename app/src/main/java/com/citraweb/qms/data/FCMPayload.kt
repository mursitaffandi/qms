package com.citraweb.qms.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class FCMPayload(
    var to: String,
    var data: Data,
    ) : Parcelable

@Parcelize
data class Data(
    var departmentName: String,
    var companyName: String,
    var priority: String,
) : Parcelable
