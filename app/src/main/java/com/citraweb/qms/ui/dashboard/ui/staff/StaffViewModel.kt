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
import com.citraweb.qms.utils.*
import com.google.firebase.Timestamp
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.UnsupportedEncodingException
import java.util.*

@ExperimentalCoroutinesApi
class StaffViewModel constructor(private val staffRepositoryImpl: StaffRepositoryImpl) : MyBaseViewModel() {

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
        _echo.value = MyApp.instance.getString(
                if (powerStatus == StateDepartment.OPEN)
                    R.string.click_staff_power_off
                else
                    R.string.click_staff_power_on
        )
    }

    fun powerLongClick(powerStatus: StateDepartment) {
        launchDataLoad {
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

    fun setQueue(newIndex: Int)  {
        launchDataLoad {
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
    // Instantiate the RequestQueue.
    val queue: RequestQueue = Volley.newRequestQueue(MyApp.instance)

    fun call(user: User, deparmentName: String, companyName: String) {
        val payload = FCMPayload(user.fcm?: "", Data(
            departmentName = deparmentName,
            companyName = companyName,
            priority = "high"
        ))

        val mRequestBody = Gson().toJson(payload).toString()

// Request a string response from the provided URL.
        val stringRequest = object : StringRequest(Method.POST, URL_FCM, { response ->
            Timber.d(response.toString())
            _echo.value = "success called ${user.name}"
        }, {
            Timber.e(it)
        }){
            override fun getHeaders(): MutableMap<String, String> {
                return mutableMapOf(
                        "Content-Type" to "application/json",
                        "Authorization" to "key=$KEY_SERVER_FCM"
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
                queue.add(stringRequest)
        }
    }

//    TODO : observer @currentQueue if has change, push fcm notif to user that has id @currentQueue
}