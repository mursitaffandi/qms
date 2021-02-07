package com.citraweb.qms.ui.login

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
import com.citraweb.qms.utils.MyBaseViewModel
import com.citraweb.qms.utils.Result
import com.citraweb.qms.utils.isEmailValid
import com.citraweb.qms.utils.isPasswordValid
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : MyBaseViewModel()  {

    private val _currentFireUserMLD = MutableLiveData<ResultData<FirebaseUser>>()
    val currentFireUserLD: LiveData<ResultData<FirebaseUser>>
        get() = _currentFireUserMLD

    private val _loginForm = MutableLiveData<RegisterFormState>()
    val loginFormState: LiveData<RegisterFormState>
        get() = _loginForm

    //Email
    fun loginUserFromAuthWithEmailAndPassword(email: String, password: String) {
        launchDataLoad {
                when (val result = userRepository.loginUserInFirestore(email, password)) {
                    is Result.Success -> {
                        _echo.value = MyApp.instance.getString(R.string.registration_successful)
                        result.data?.let {
                            _currentFireUserMLD.value = ResultData(success = it, message = R.string.registration_successful)
                        }
                    }
                    is Result.Error -> {
                        _currentFireUserMLD.value = ResultData(message = R.string.register_failed)
                        _echo.value = result.exception.message
                    }
                    is Result.Canceled -> {
                        _echo.value = MyApp.instance.getString(R.string.request_canceled)
                    }
            }
        }
    }

    fun loginDataChanged(email: String, password: String) {
        val errorFormState = RegisterFormState()
        if (!isEmailValid(email)) {
            errorFormState.emailError = R.string.invalid_email
        }
        if (!isPasswordValid(password)) {
            errorFormState.passwordError = R.string.invalid_password
        }

        if (
            errorFormState.emailError == null &&
            errorFormState.passwordError == null
        ) errorFormState.isDataValid = true

        _loginForm.value = errorFormState

    }
}