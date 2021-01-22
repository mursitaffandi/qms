package com.citraweb.qms.ui.forgetpassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citraweb.qms.MyApp
import com.citraweb.qms.R
import com.citraweb.qms.data.ResultData
import com.citraweb.qms.data.user.User
import com.citraweb.qms.data.user.UserRepository
import com.citraweb.qms.ui.register.RegisterFormState
import com.citraweb.qms.utils.Result
import com.citraweb.qms.utils.isEmailValid
import com.citraweb.qms.utils.isPasswordValid
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ForgetPasswordViewModel(private val userRepository: UserRepository) : ViewModel()  {
    private val _echo = MutableLiveData<String?>()
    val toast: LiveData<String?>
        get() = _echo

    private val _loading = MutableLiveData(false)
    val spinner: LiveData<Boolean>
        get() = _loading

    private val _currentUserMLD = MutableLiveData<ResultData<User>>()
    val currentUserLD: LiveData<ResultData<User>>
        get() = _currentUserMLD

    private val _registerForm = MutableLiveData<RegisterFormState>()
    val registerFormState: LiveData<RegisterFormState>
        get() = _registerForm

    //Email
    fun loginUserFromAuthWithEmailAndPassword(email: String, password: String) {
        launchDataLoad {
            viewModelScope.launch {
                when (val result =
                    userRepository.loginUserInFirestore(email, password)) {
                    is Result.Success -> {
                        result.data?.let { firebaseUser ->
                            createUserInFirestore(createUserObject(firebaseUser))
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

    private suspend fun createUserInFirestore(user: User) {
        when (val result = userRepository.createUserInFirestore(user)) {
            is Result.Success -> {
                _echo.value = MyApp.instance.getString(R.string.login_successful)
                _currentUserMLD.value = ResultData<User>(success = user, message = R.string.login_successful)
            }
            is Result.Error -> {
                _currentUserMLD.value = ResultData<User>(message = R.string.login_failed)
                _echo.value = result.exception.message
            }
            is Result.Canceled -> {
                _echo.value = MyApp.instance.getString(R.string.request_canceled)
            }
        }
    }


    private fun createUserObject(
        firebaseUser: FirebaseUser
    ): User {
        return User(
            id = firebaseUser.uid,
            name = firebaseUser.displayName,
            email = firebaseUser.email
        )
    }

    fun onToastShown() {
        _echo.value = null
    }

    private fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                _loading.value = true
                block()
            } catch (error: Throwable) {
                _echo.value = error.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun loginDataChanged(email: String) {
        val errorFormState = RegisterFormState()
        if (!isEmailValid(email)) {
            errorFormState.emailError = R.string.invalid_email
        }


        if (
            errorFormState.emailError == null
        ) errorFormState.isDataValid = true

        _registerForm.value = errorFormState

    }
}