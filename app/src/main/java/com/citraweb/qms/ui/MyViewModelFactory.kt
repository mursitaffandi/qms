package com.citraweb.qms.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.citraweb.qms.data.user.UserRepositoryImpl
import com.citraweb.qms.ui.dashboard.DashboardViewModel
import com.citraweb.qms.ui.login.LoginViewModel
import com.citraweb.qms.ui.register.RegisterViewModel

@Suppress("UNCHECKED_CAST")
class MyViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) return LoginViewModel(UserRepositoryImpl()) as T

        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) return RegisterViewModel(UserRepositoryImpl()) as T

        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) return DashboardViewModel(UserRepositoryImpl()) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}