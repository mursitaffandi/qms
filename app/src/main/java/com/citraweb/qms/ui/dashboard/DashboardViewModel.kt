package com.citraweb.qms.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citraweb.qms.R
import com.citraweb.qms.data.ResultData
import com.citraweb.qms.data.user.User
import com.citraweb.qms.data.user.UserRepository
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
            val user = repository.getUserInFirestore()
            if (user!=null)
                _currentUserMLD.value = ResultData(
                        success = User(
                                userId = user.uid,
                                name = user.displayName,
                                email = user.email
                        ),
                        message = R.string.login_successful
                )
             else _currentUserMLD.value = ResultData<User>(
                    success = null,
                    message = R.string.unknow_user
             )

        }
    }
}