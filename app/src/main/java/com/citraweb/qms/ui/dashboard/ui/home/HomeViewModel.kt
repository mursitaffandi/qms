package com.citraweb.qms.ui.dashboard.ui.home

import androidx.lifecycle.ViewModel
import com.citraweb.qms.data.model.User
import com.citraweb.qms.repository.UserRepository

class HomeViewModel(private val userRepository: UserRepository) : ViewModel()  {

    fun getUser() : User? {
        userRepository.getUserInFirestore()?.let {
            return User(
                id = it.uid,
                name = it.displayName,
                email = it.email
            )
        }
        return null
    }
 }