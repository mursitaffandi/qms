package com.citraweb.qms.ui.dashboard.ui.departments

import com.citraweb.qms.data.queue.QueueRepository
import com.citraweb.qms.utils.MyBaseViewModel
import com.citraweb.qms.utils.Result

class DepartmentsViewModel(private val repository : QueueRepository) : MyBaseViewModel(){

    val query = repository.getQueryDepartment()

    fun join(it: Int?, id: String) {
        launchDataLoad {
            when(repository.joinQueue(id, it?:0)){
                is Result.Canceled -> {}
                is Result.Error -> {}
                is Result.Success -> {}
            }
        }

    }
}