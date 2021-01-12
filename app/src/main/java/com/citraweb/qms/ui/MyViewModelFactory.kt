package com.citraweb.qms.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.citraweb.qms.repository.implementation.UserRepositoryImpl
import com.citraweb.qms.ui.login.LoginViewModel
import com.citraweb.qms.ui.register.RegisterViewModel

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class MyViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                UserRepositoryImpl()
            ) as T
        }

        if (modelClass.isAssignableFrom(RegisterViewModel::class.java))
            return RegisterViewModel(UserRepositoryImpl()) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}