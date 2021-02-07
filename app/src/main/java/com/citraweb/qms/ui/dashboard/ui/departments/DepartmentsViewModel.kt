package com.citraweb.qms.ui.dashboard.ui.departments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.citraweb.qms.data.department.Department
import com.citraweb.qms.data.queue.QueueRepository
import com.citraweb.qms.utils.MyBaseViewModel
import com.citraweb.qms.utils.Result

class DepartmentsViewModel(private val repository : QueueRepository) : MyBaseViewModel(){

    val query = repository.getQueryDepartment()

    fun join(it: Department, id: String) {
        launchDataLoad {
            repository.joinQueue(id)
        }

    }
}