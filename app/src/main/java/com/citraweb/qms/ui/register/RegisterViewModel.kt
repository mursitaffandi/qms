package com.citraweb.qms.ui.register

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
import com.citraweb.qms.utils.isEmailValid
import com.citraweb.qms.utils.isPasswordValid
import com.citraweb.qms.utils.isUserNameValid
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {
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
    fun registerUserFromAuthWithEmailAndPassword(name: String, email: String, password: String) {
        launchDataLoad {
            viewModelScope.launch {
                when (val result =
                        userRepository.registerUserFromAuthWithEmailAndPassword(name, email, password)) {
                    is Result.Success -> {
                        result.data?.let { firebaseUser ->
                            createUserInFirestore(User(firebaseUser, name))
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
                _echo.value = MyApp.instance.getString(R.string.registration_successful)
                _currentUserMLD.value = ResultData<User>(success = user, message = R.string.registration_successful)
            }
            is Result.Error -> {
                _currentUserMLD.value = ResultData<User>(message = R.string.register_failed)
                _echo.value = result.exception.message
            }
            is Result.Canceled -> {
                _echo.value = MyApp.instance.getString(R.string.request_canceled)
            }
        }
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

    fun registerDataChanged(username: String, email: String, password: String, confirmPassword: String) {
        val errorFormState = RegisterFormState()
        if (!isUserNameValid(username)) {
            errorFormState.usernameError = R.string.invalid_username
        }
        if (!isEmailValid(email)) {
            errorFormState.emailError = R.string.invalid_email
        }
        if (!isPasswordValid(password)) {
            errorFormState.passwordError = R.string.invalid_password
        }
        if (password != confirmPassword) {
            errorFormState.confirmPasswordError = R.string.invalid_confirm_password
        }

        if (
            errorFormState.usernameError == null &&
            errorFormState.emailError == null &&
            errorFormState.passwordError == null &&
            errorFormState.confirmPasswordError == null
        ) errorFormState.isDataValid = true

        _registerForm.value = errorFormState

    }
}