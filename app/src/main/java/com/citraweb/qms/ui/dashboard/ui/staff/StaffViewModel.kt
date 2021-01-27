package com.citraweb.qms.ui.dashboard.ui.staff

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.citraweb.qms.R
import com.citraweb.qms.data.department.StaffRepositoryImpl
import com.citraweb.qms.data.user.User
import com.citraweb.qms.utils.MyBaseViewModel
import com.citraweb.qms.utils.StateDepartment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.coroutines.launch

class StaffViewModel(private val ctx: Context, private val staffRepositoryImpl: StaffRepositoryImpl) : MyBaseViewModel() {
    val query: FirestoreRecyclerOptions<User?> = staffRepositoryImpl.getQueryQueue()

    private val _state = MutableLiveData<StateDepartment>()
    val state: LiveData<StateDepartment>
        get() = _state

    private val _currentQueue = MutableLiveData<StateDepartment>()
    val currentQueue: LiveData<StateDepartment>
        get() = _currentQueue

    private val _amount = MutableLiveData<StateDepartment>()
    val amount: LiveData<StateDepartment>
        get() = _amount

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

    fun next() {
        launchDataLoad {
            viewModelScope.launch {
//            TODO : if @currentQueue not last, update @currentQueue++
            }
        }
    }

//    TODO : observer @currentQueue if has change, push fcm notif to user that has id @currentQueue
}