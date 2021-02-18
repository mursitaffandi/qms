package com.citraweb.qms.ui.dashboard.ui.departments

import com.citraweb.qms.data.department.Department
import com.citraweb.qms.data.queue.QueueRepository
import com.citraweb.qms.utils.MyBaseViewModel
import com.citraweb.qms.utils.Result
import timber.log.Timber

class DepartmentsViewModel(private val repository : QueueRepository) : MyBaseViewModel(){

    val query = repository.getQueryDepartment()

    fun join(idDepartment: String, idUser: String, it: Department) {
        launchDataLoad {
            when(val join = repository.joinQueue(idDepartment, idUser, it)){
                is Result.Canceled -> {
                    Timber.e(join.exception)
                }
                is Result.Error -> {
                    Timber.e(join.exception)
                }
                is Result.Success -> {}
            }
        }

    }
}