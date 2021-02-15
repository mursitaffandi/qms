package com.citraweb.qms.ui.dashboard.ui.departments

import com.citraweb.qms.data.department.Department
import com.citraweb.qms.data.queue.QueueRepository
import com.citraweb.qms.utils.MyBaseViewModel
import com.citraweb.qms.utils.Result

class DepartmentsViewModel(private val repository : QueueRepository) : MyBaseViewModel(){

    val query = repository.getQueryDepartment()

    fun join(idDepartment: String, idUser: String, it: Department) {
        launchDataLoad {
            when(repository.joinQueue(idDepartment, idUser, it)){
                is Result.Canceled -> {}
                is Result.Error -> {}
                is Result.Success -> {}
            }
        }

    }
}