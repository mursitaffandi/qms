package com.citraweb.qms.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citraweb.qms.R
import com.citraweb.qms.data.model.User
import com.citraweb.qms.repository.UserRepository
import com.citraweb.qms.repository.UserResult
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository : UserRepository) : ViewModel(){
    private val _echo = MutableLiveData<String?>()
    val toast: LiveData<String?>
        get() = _echo

    private val _loading = MutableLiveData(false)
    val spinner: LiveData<Boolean>
        get() = _loading

    private val _currentUserMLD = MutableLiveData<UserResult>()
    val currentUserLD: LiveData<UserResult>
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
                _currentUserMLD.value = UserResult(
                        success = User(
                                id = user.uid,
                                name = user.displayName,
                                email = user.email
                        ),
                        message = R.string.login_successful
                )
             else _currentUserMLD.value = UserResult(
                    success = null,
                    message = R.string.unknow_user
             )

        }
    }
}