package com.citraweb.qms.ui.dashboard.ui.staff

import androidx.lifecycle.liveData
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.citraweb.qms.MyApp
import com.citraweb.qms.R
import com.citraweb.qms.data.Data
import com.citraweb.qms.data.FCMPayload
import com.citraweb.qms.data.department.StaffRepositoryImpl
import com.citraweb.qms.utils.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import java.io.UnsupportedEncodingException

@ExperimentalCoroutinesApi
class StaffViewModel constructor(private val staffRepositoryImpl: StaffRepositoryImpl) : MyBaseViewModel() {

    val query = staffRepositoryImpl.getQueryQueue()
    // Instantiate the RequestQueue.
    private val queue: RequestQueue = Volley.newRequestQueue(MyApp.instance)

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

    fun powerLongClick(powerStatus: StateDepartment, name: String, company: String, prefix: String) {
        launchDataLoad {
            val index = powerStatus.ordinal + 1
            when (staffRepositoryImpl.updateDepartement(StateDepartment.values()[index % 2], name, company, prefix)) {
                is Result.Success -> { }
                is Result.Error -> { }
                is Result.Canceled -> { }
            }
        }
    }

    fun setQueue(newIndex: Int, idMember : String, deparmentName: String?, companyName: String?) {
        launchDataLoad {
            when (staffRepositoryImpl.nextQueue(newIndex)) {
                is Result.Success -> {
                    call(idMember, deparmentName, companyName)
                    staffRepositoryImpl.updateWaiting()
                }
                is Result.Error -> { }
                is Result.Canceled -> { }
            }
        }
    }

    fun call(idUser: String, departmentName: String?, companyName: String?) {
        launchDataLoad {
            when (val fcm = staffRepositoryImpl.getFcm(idUser)) {
                is Result.Canceled -> Timber.e(fcm.exception)
                is Result.Error -> Timber.e(fcm.exception)
                is Result.Success -> {
                    when(staffRepositoryImpl.updateQueueStatus(idUser)){
                        is Result.Canceled -> {}
                        is Result.Error -> {}
                        is Result.Success -> {}
                    }
                    val payload = FCMPayload(fcm.data ?: "", Data(
                            departmentName = departmentName?:"",
                            companyName = companyName?:"",
                            priority = "high"
                    ))

                    val mRequestBody = Gson().toJson(payload).toString()

                    // Request a string response from the provided URL.
                    val stringRequest = object : StringRequest(Method.POST, URL_FCM, { response ->
                        Timber.d(response.toString())
                    }, {
                        Timber.e(it)
                    }) {
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
                    queue.add(stringRequest)
                }
            }
        }
    }

//    TODO : observer @currentQueue if has change, push fcm notif to user that has id @currentQueue
}