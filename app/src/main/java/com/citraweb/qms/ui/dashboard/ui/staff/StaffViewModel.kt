package com.citraweb.qms.ui.dashboard.ui.staff

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.citraweb.qms.R
import com.citraweb.qms.data.department.StaffRepositoryImpl
import com.citraweb.qms.utils.StateDepartment
import com.citraweb.qms.utils.toas

class StaffViewModel(private val ctx: Context, staffRepositoryImpl: StaffRepositoryImpl) : ViewModel() {
    private val _loket = MutableLiveData<StateDepartment>()
    val loket: LiveData<StateDepartment>
        get() = _loket

    private val _echo = MutableLiveData<String?>()
    val toast: LiveData<String?>
        get() = _echo

    private val _loading = MutableLiveData(false)
    val spinner: LiveData<Boolean>
        get() = _loading


    fun powerClick() {
        _echo.value = ctx.getString(
                if (_loket.value == StateDepartment.OPEN)
                    R.string.click_staff_power_off
                else
                    R.string.click_staff_power_on
        )
    }

    fun powerLongClick() {

    }


}