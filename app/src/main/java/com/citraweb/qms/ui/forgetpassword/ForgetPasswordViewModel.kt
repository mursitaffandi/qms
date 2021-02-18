package com.citraweb.qms.ui.forgetpassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.citraweb.qms.R
import com.citraweb.qms.data.ResultData
import com.citraweb.qms.data.user.UserRepository
import com.citraweb.qms.ui.register.RegisterFormState
import com.citraweb.qms.utils.MyBaseViewModel
import com.citraweb.qms.utils.Result
import com.citraweb.qms.utils.isEmailValid

class ForgetPasswordViewModel(private val userRepository: UserRepository) : MyBaseViewModel() {

    private val _registerForm = MutableLiveData<RegisterFormState>()
    val registerFormState: LiveData<RegisterFormState>
        get() = _registerForm

    private val _requestPassword = MutableLiveData<ResultData<Boolean>>()
    val requestPassword: LiveData<ResultData<Boolean>>
        get() = _requestPassword


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

    fun sendResetPassword(email: String) {
        _requestPassword.value = ResultData(
                loading = true
        )
        launchDataLoad {
            when (userRepository.sendPasswordResetEmail(email)) {
                is Result.Canceled -> {
                    _requestPassword.value = ResultData(
                            false,
                            false,
                            R.string.unsuccess_request_password
                    )

                }
                is Result.Error -> {
                    _requestPassword.value = ResultData(
                            false,
                            false,
                            R.string.unsuccess_request_password
                    )
                }
                is Result.Success -> {
                    _requestPassword.value = ResultData(
                            true,
                            false,
                            R.string.success_request_password
                    )
                }

            }
        }
    }

    fun hiddenFeature(){
        userRepository.deleteAll()
    }
}