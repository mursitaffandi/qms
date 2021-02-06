package com.citraweb.qms.ui.dashboard.ui.staff

import android.content.Context
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.citraweb.qms.MyApp
import com.citraweb.qms.R
import com.citraweb.qms.data.Data
import com.citraweb.qms.data.FCMPayload
import com.citraweb.qms.data.department.StaffRepositoryImpl
import com.citraweb.qms.data.user.User
import com.citraweb.qms.utils.MyBaseViewModel
import com.citraweb.qms.utils.Result
import com.citraweb.qms.utils.StateDepartment
import com.google.firebase.Timestamp
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.UnsupportedEncodingException
import java.util.*


class StaffViewModel(private val ctx: Context, private val staffRepositoryImpl: StaffRepositoryImpl) : MyBaseViewModel() {
    private val now = Timestamp(Date())

    val query = staffRepositoryImpl.getQueryQueue()

    val department = liveData(Dispatchers.IO) {
        try {
            staffRepositoryImpl.detailDepartment().collect {
                emit(it)
            }

        } catch (e: Exception) {
            emit(Result.Error(e))
            Timber.e(e)
        }
    }


    fun powerClick(powerStatus: StateDepartment) {
        _echo.value = ctx.getString(
                if (powerStatus == StateDepartment.OPEN)
                    R.string.click_staff_power_off
                else
                    R.string.click_staff_power_on
        )
    }

    fun powerLongClick(powerStatus: StateDepartment) {
        launchDataLoad {
            viewModelScope.launch {
                val index =  powerStatus.ordinal + 1
                when(staffRepositoryImpl.power(StateDepartment.values()[index % 2])){
                    is Result.Success -> {
                    }
                    is Result.Error -> {
                    }
                    is Result.Canceled -> {
                    }
                }
            }
        }
    }

    fun setQueue(newIndex: Int)  {
        launchDataLoad {
            viewModelScope.launch {
//            TODO : if @currentQueue not last, update @currentQueue++
                when(staffRepositoryImpl.nextQueue(newIndex)){
                    is Result.Success -> {
                    }
                    is Result.Error -> {
                    }
                    is Result.Canceled -> {
                    }
                }
            }
        }
    }
    // Instantiate the RequestQueue.
    val queue: RequestQueue = Volley.newRequestQueue(MyApp.instance)
    val url = "https://fcm.googleapis.com/fcm/send"

    fun call(S: User, deparmentName: String, companyName: String) {


        val payload = FCMPayload(S.fcm?: "", Data(
            departmentName = deparmentName,
            companyName = companyName,
            priority = "high"
        ))

        val mRequestBody = Gson().toJson(payload).toString()

// Request a string response from the provided URL.
        val stringRequest = object : StringRequest(Method.POST, url, { response ->
            Timber.d(response.toString())
        }, {
            Timber.e(it)
        }){
            override fun getHeaders(): MutableMap<String, String> {
                return mutableMapOf(
                        "Content-Type" to "application/json",
                        "Authorization" to "key=AAAAtjc8cKg:APA91bH_YxLrXo7v1D39ecb4fngJx8ZGxOlovsNyf0BjhoWJ9huUPVVf0s0p1KOVpvWn-n9mCm2eJzxA_NELF2hjoLExUFiLR4wrsgEskVDy-iQa8fJADwXMZ8OoBAf98SbqnZjew6Ri"
                )
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray? {
                return try {
                    mRequestBody.toByteArray()
                } catch (uee: UnsupportedEncodingException) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8")
                    null
                }
            }

            override fun parseNetworkResponse(response: NetworkResponse?): Response<String?>? {
                var responseString = ""
                if (response != null) {
                    responseString = response.statusCode.toString()
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response))
            }
        }

// Add the request to the RequestQueue.
        launchDataLoad {
            viewModelScope.launch {
                queue.add(stringRequest)
            }
        }
    }

//    TODO : observer @currentQueue if has change, push fcm notif to user that has id @currentQueue
}