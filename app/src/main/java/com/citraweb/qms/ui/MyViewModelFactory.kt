package com.citraweb.qms.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.citraweb.qms.MyApp
import com.citraweb.qms.data.department.StaffRepositoryImpl
import com.citraweb.qms.data.queue.QueueRepositoryImpl
import com.citraweb.qms.data.user.UserRepositoryImpl
import com.citraweb.qms.ui.dashboard.DashboardViewModel
import com.citraweb.qms.ui.dashboard.ui.departments.DepartmentsViewModel
import com.citraweb.qms.ui.dashboard.ui.staff.StaffViewModel
import com.citraweb.qms.ui.forgetpassword.ForgetPasswordViewModel
import com.citraweb.qms.ui.login.LoginViewModel
import com.citraweb.qms.ui.register.RegisterViewModel

@Suppress("UNCHECKED_CAST")
class MyViewModelFactory : ViewModelProvider.Factory {

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

        if (modelClass.isAssignableFrom(StaffViewModel::class.java)) return StaffViewModel(MyApp.instance,
            StaffRepositoryImpl("xhANVssutCWtgKz79SQXWYuVnBy1")
//        TODO : change to variable
        ) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}