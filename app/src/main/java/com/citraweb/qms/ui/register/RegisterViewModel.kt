package com.citraweb.qms.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citraweb.qms.MyApp
import com.citraweb.qms.R
import com.citraweb.qms.data.model.User
import com.citraweb.qms.repository.UserRepository
import com.citraweb.qms.utils.isPasswordValid
import com.citraweb.qms.utils.isUserNameValid
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.citraweb.qms.utils.Result


class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _echo = MutableLiveData<String?>()
    val toast: LiveData<String?>
        get() = _echo

    private val _loading = MutableLiveData(false)
    val spinner: LiveData<Boolean>
        get() = _loading

    private val _currentUserMLD = MutableLiveData<RegisterResult>()
    val currentUserLD: LiveData<RegisterResult>
        get() = _currentUserMLD

    private val _registerForm = MutableLiveData<RegisterFormState>()
    val registerFormState: LiveData<RegisterFormState>
        get() = _registerForm

    //Email
    fun registerUserFromAuthWithEmailAndPassword(name: String, email: String, password: String) {
        launchDataLoad {
            viewModelScope.launch {
                when (val result =
                    userRepository.registerUserFromAuthWithEmailAndPassword(email, password)) {
                    is Result.Success -> {
                        result.data?.let { firebaseUser ->
                            createUserInFirestore(createUserObject(firebaseUser, name))
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
                    _currentUserMLD.value = RegisterResult(success = user, message = R.string.registration_successful)
//              startMainActivitiy(activity)
            }
            is Result.Error -> {
                _currentUserMLD.value = RegisterResult(message = R.string.register_failed)
                _echo.value = result.exception.message
            }
            is Result.Canceled -> {
                _echo.value = MyApp.instance.getString(R.string.request_canceled)
            }
        }
    }


    fun createUserObject(
        firebaseUser: FirebaseUser,
        name: String,
        profilePicture: String = ""
    ): User {
        return User(
            id = firebaseUser.uid,
            name = name,
            profilePicture = profilePicture
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

    fun registerDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _registerForm.value = RegisterFormState(emailError = R.string.invalid_email)
        } else if (!isPasswordValid(password)) {
            _registerForm.value = RegisterFormState(passwordError = R.string.invalid_password)
        } else {
            _registerForm.value = RegisterFormState(isDataValid = true)
        }
    }
}