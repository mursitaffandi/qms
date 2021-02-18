package com.citraweb.qms.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.citraweb.qms.data.department.StaffRepositoryImpl
import com.citraweb.qms.data.queue.QueueRepositoryImpl
import com.citraweb.qms.data.user.UserRepositoryImpl
import com.citraweb.qms.ui.dashboard.DashboardViewModel
import com.citraweb.qms.ui.dashboard.ui.departments.DepartmentsViewModel
import com.citraweb.qms.ui.dashboard.ui.queue.MyTicketViewModel
import com.citraweb.qms.ui.dashboard.ui.staff.StaffViewModel
import com.citraweb.qms.ui.forgetpassword.ForgetPasswordViewModel
import com.citraweb.qms.ui.login.LoginViewModel
import com.citraweb.qms.ui.register.RegisterViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Suppress("UNCHECKED_CAST")
class MyViewModelFactory : ViewModelProvider.Factory {

    @ExperimentalCoroutinesApi
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) return LoginViewModel(
            UserRepositoryImpl()
        ) as T
        if (modelClass.isAssignableFrom(ForgetPasswordViewModel::class.java)) return ForgetPasswordViewModel(
            UserRepositoryImpl()
        ) as T

        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) return RegisterViewModel(
            UserRepositoryImpl()
        ) as T

        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) return DashboardViewModel(
            UserRepositoryImpl()
        ) as T

        if (modelClass.isAssignableFrom(DepartmentsViewModel::class.java)) return DepartmentsViewModel(
            QueueRepositoryImpl()
        ) as T

        if (modelClass.isAssignableFrom(MyTicketViewModel::class.java)) return MyTicketViewModel(
            QueueRepositoryImpl()
        ) as T

        if (modelClass.isAssignableFrom(StaffViewModel::class.java)) return StaffViewModel(
            StaffRepositoryImpl()
//        TODO : change to variable
        ) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}