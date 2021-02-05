package com.citraweb.qms.ui.dashboard.ui.staff

import android.content.Context
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.citraweb.qms.R
import com.citraweb.qms.data.department.StaffRepositoryImpl
import com.citraweb.qms.utils.MyBaseViewModel
import com.citraweb.qms.utils.Result
import com.citraweb.qms.utils.StateDepartment
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
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
                    is Result.Success -> {}
                    is Result.Error -> {}
                    is Result.Canceled -> {}
                }
            }
        }
    }

    fun setQueue(newIndex : Int)  {
        launchDataLoad {
            viewModelScope.launch {
//            TODO : if @currentQueue not last, update @currentQueue++
                when(staffRepositoryImpl.nextQueue(newIndex)){
                    is Result.Success -> {}
                    is Result.Error -> {}
                    is Result.Canceled -> {}
                }
            }
        }
    }

//    TODO : observer @currentQueue if has change, push fcm notif to user that has id @currentQueue
}