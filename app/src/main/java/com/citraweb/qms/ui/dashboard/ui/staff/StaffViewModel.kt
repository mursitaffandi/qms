package com.citraweb.qms.ui.dashboard.ui.staff

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.citraweb.qms.R
import com.citraweb.qms.data.department.Department
import com.citraweb.qms.data.department.StaffRepositoryImpl
import com.citraweb.qms.data.user.User
import com.citraweb.qms.utils.MyBaseViewModel
import com.citraweb.qms.utils.Result
import com.citraweb.qms.utils.StateDepartment
import com.citraweb.qms.utils.await
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.util.*

class StaffViewModel(private val ctx: Context, private val staffRepositoryImpl: StaffRepositoryImpl) : MyBaseViewModel() {
    private val sekarang = Timestamp(Date())

    val query = staffRepositoryImpl.getQueryQueue()

    private val _department = MutableLiveData<Department>()
    val department: LiveData<Department>
        get() = _department

    private val _state = MutableLiveData<StateDepartment>()
    val state: LiveData<StateDepartment>
        get() = _state

    fun start() {
        viewModelScope.launch {
            when (val hotDepartment = staffRepositoryImpl.detailDepartment()?.await()) {
                is Result.Success -> {
                    val data = hotDepartment.data
                    _department.value = data
                    _state.value = data?.status?.let { StateDepartment.valueOf(it) }
                }
            }
        }
    }

    fun powerClick() {
        _echo.value = ctx.getString(
                if (_state.value == StateDepartment.OPEN)
                    R.string.click_staff_power_off
                else
                    R.string.click_staff_power_on
        )
    }

    fun powerLongClick(action: StateDepartment) {
        launchDataLoad {
            viewModelScope.launch {
                staffRepositoryImpl.power(action)
            }
        }
    }

    /*fun setQueue() {
        launchDataLoad {
            viewModelScope.launch {
//            TODO : if @currentQueue not last, update @currentQueue++
                staffRepositoryImpl.
            }
        }
    }*/

//    TODO : observer @currentQueue if has change, push fcm notif to user that has id @currentQueue
}