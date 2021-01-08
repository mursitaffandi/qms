package com.citraweb.qms.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.citraweb.qms.data.LoginDataSource
import com.citraweb.qms.data.LoginRepository
import com.citraweb.qms.ui.register.RegisterViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class LoginViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                    loginRepository = LoginRepository(
                            dataSource = LoginDataSource()
                    )
            ) as T
        }

        if (modelClass.isAssignableFrom(RegisterViewModel::class.java))
            return RegisterViewModel(Firebase.auth) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}