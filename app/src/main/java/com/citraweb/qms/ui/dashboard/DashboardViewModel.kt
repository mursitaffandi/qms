package com.citraweb.qms.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.citraweb.qms.MyApp
import com.citraweb.qms.R
import com.citraweb.qms.data.ResultData
import com.citraweb.qms.data.department.StaffRepositoryImpl.Companion.getDepartmentId
import com.citraweb.qms.data.user.User
import com.citraweb.qms.data.user.UserRepository
import com.citraweb.qms.utils.MyBaseViewModel
import com.citraweb.qms.utils.Result
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: UserRepository) : MyBaseViewModel() {

    private val _currentUserMLD = MutableLiveData<ResultData<User>>()
    val currentUserLD: LiveData<ResultData<User>>
        get() = _currentUserMLD

    fun start() {
        checkCredential()
    }

    fun revoke() {
        repository.logoutUserInFirestore()
        checkCredential()
    }

    private fun checkCredential() {
        viewModelScope.launch {
            when (val result = repository.getUserInFirestore()) {
                is Result.Success -> {
                    MyApp.idDocumentUser = result.data?.userId
                    result.data?.let { firestoreUser ->
                        _currentUserMLD.value = ResultData(
                                success = firestoreUser,
                                message = R.string.login_successful
                        )
                    }
                }
                is Result.Error -> {
                    _currentUserMLD.value = ResultData(
                            success = null,
                            message = R.string.logout
                    )
                    _echo.value = result.exception.message
                }
                is Result.Canceled -> {
                    _echo.value = MyApp.instance.getString(R.string.request_canceled)
                }
            }

            when (val result = getDepartmentId()) {
                is Result.Success -> {
                    MyApp.idDocumentDepartment = result.data
                }
            }
        }
    }
}