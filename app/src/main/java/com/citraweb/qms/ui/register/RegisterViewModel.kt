package com.citraweb.qms.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.citraweb.qms.MyApp
import com.citraweb.qms.R
import com.citraweb.qms.data.ResultData
import com.citraweb.qms.data.user.User
import com.citraweb.qms.data.user.UserRepository
import com.citraweb.qms.utils.*
import kotlinx.coroutines.launch


class RegisterViewModel(private val userRepository: UserRepository) : MyBaseViewModel() {

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
                            createDepartmnetInFirestore(User(firebaseUser, name))
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

    private suspend fun createDepartmnetInFirestore(user: User) {
        when (val result = userRepository.createDepartmnetInFirestore(user)) {
            is Result.Success -> {
                result.data?.id
                _echo.value = MyApp.instance.getString(R.string.registration_successful)
                _currentUserMLD.value = ResultData(success = user, message = R.string.registration_successful)
            }
            is Result.Error -> {
                _currentUserMLD.value = ResultData(message = R.string.register_failed)
                _echo.value = result.exception.message
            }
            is Result.Canceled -> {
                _echo.value = MyApp.instance.getString(R.string.request_canceled)
            }
        }
    }

    private suspend fun createUserInFirestore(user: User) {
        when (val result = userRepository.createUserInFirestore(user)) {
            is Result.Success -> {
                _echo.value = MyApp.instance.getString(R.string.registration_successful)
                _currentUserMLD.value = ResultData(success = user, message = R.string.registration_successful)
            }
            is Result.Error -> {
                _currentUserMLD.value = ResultData(message = R.string.register_failed)
                _echo.value = result.exception.message
            }
            is Result.Canceled -> {
                _echo.value = MyApp.instance.getString(R.string.request_canceled)
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