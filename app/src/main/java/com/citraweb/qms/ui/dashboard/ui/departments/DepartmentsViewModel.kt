package com.citraweb.qms.ui.dashboard.ui.departments

import androidx.lifecycle.liveData
import com.citraweb.qms.data.queue.QueueRepository
import com.citraweb.qms.utils.MyBaseViewModel
import com.citraweb.qms.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import timber.log.Timber

class DepartmentsViewModel(private val repository : QueueRepository) : MyBaseViewModel(){

    val query = repository.getQueryDepartment()
    val user = liveData(Dispatchers.IO) {
        try {
            repository.detailUser().collect {
                emit(it)
            }

        } catch (e: Exception) {
            emit(Result.Error(e))
            Timber.e(e)
        }
    }

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