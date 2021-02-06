package com.citraweb.qms.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.citraweb.qms.MyApp
import com.citraweb.qms.R
import com.citraweb.qms.data.ResultData
import com.citraweb.qms.data.user.User
import com.citraweb.qms.data.user.UserRepository
import com.citraweb.qms.data.user.UserRepositoryImpl.Companion.updateFcmToken
import com.citraweb.qms.utils.MyBaseViewModel
import com.citraweb.qms.utils.Result
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

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
                    result.data?.let { firestoreUser ->
                        firestoreUser.departmentId?.let {
                            repository.setDepartmentId(it)
                        }
                        _currentUserMLD.value = ResultData(
                            success = firestoreUser,
                            message = R.string.login_successful
                        )

                        FirebaseMessaging.getInstance().token.addOnCompleteListener(
                            OnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    Log.w(
                                        "FirebaseMessaging",
                                        "Fetching FCM registration token failed",
                                        task.exception
                                    )
                                    return@OnCompleteListener
                                }
                                // Get new FCM registration token
                                val token = task.result
                                Timber.d(token)
                                GlobalScope.launch {
                                    token?.let { updateFcmToken(it) }
                                }
                            })
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

        }
    }
}