package com.citraweb.qms.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citraweb.qms.MyApp
import com.citraweb.qms.R
import com.citraweb.qms.data.ResultData
import com.citraweb.qms.data.user.User
import com.citraweb.qms.data.user.UserRepository
import com.citraweb.qms.utils.Result
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository : UserRepository) : ViewModel(){
    private val _echo = MutableLiveData<String?>()
    val toast: LiveData<String?>
        get() = _echo

    private val _loading = MutableLiveData(false)
    val spinner: LiveData<Boolean>
        get() = _loading

    private val _currentUserMLD = MutableLiveData<ResultData<User>>()
    val currentUserLD: LiveData<ResultData<User>>
        get() = _currentUserMLD

    fun start(){
        checkCredential()
    }

    fun revoke(){
        viewModelScope.launch {
            repository.logoutUserInFirestore()
        }
        checkCredential()
    }

    private fun checkCredential(){
        viewModelScope.launch {
            when (val result = repository.getUserInFirestore()) {
                is Result.Success -> {
                    result.data?.let { firestoreUser ->
                        _currentUserMLD.value = ResultData(
                                success = firestoreUser,
                                message = R.string.login_successful
                        )
                    }
                }
                is Result.Error -> {
                    _echo.value = result.exception.message
                }
                is Result.Canceled -> {
                    _echo.value = MyApp.instance.getString(R.string.request_canceled)
                }
            }
        }
    }
}