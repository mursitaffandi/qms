package com.citraweb.qms.ui.dashboard.ui.departments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citraweb.qms.R
import com.citraweb.qms.data.ResultData
import com.citraweb.qms.data.queue.Queue
import com.citraweb.qms.data.queue.QueueRepository
import com.citraweb.qms.utils.Result
import kotlinx.coroutines.launch

class DepartmentsViewModel(private val repository : QueueRepository) : ViewModel(){
    private val _echo = MutableLiveData<String?>()
    val toast: LiveData<String?>
        get() = _echo

    private val _loading = MutableLiveData(false)
    val spinner: LiveData<Boolean>
        get() = _loading

    private val _currentDepartmentsMLD = MutableLiveData<ResultData<List<Queue>>>()
    val currentDepartmentsLD: MutableLiveData<ResultData<List<Queue>>>
        get() = _currentDepartmentsMLD

    fun getDepartments(){
        viewModelScope.launch {
            when(val result = repository.getQueues()){
                is Result.Success -> {
                    _currentDepartmentsMLD.value = ResultData(
                        success = result.data,
                        message = R.string.success_departmens
                    )
                }
                is Result.Error -> {
                    _currentDepartmentsMLD.value = ResultData(
                        success = null,
                        message = R.string.fail_departmens
                    )
                }
                is Result.Canceled -> {
                    _currentDepartmentsMLD.value = ResultData(
                        success = null,
                        message = R.string.request_canceled
                    )
                }
            }
        }
    }

    fun join(it: Queue) {
        TODO("Not yet implemented")
    }
}