package com.citraweb.qms.repository

import com.citraweb.qms.data.model.User
import com.citraweb.qms.utils.Result
import com.google.firebase.auth.FirebaseUser


interface UserRepository
{
    suspend fun registerUserFromAuthWithEmailAndPassword(
        name: String,
        email: String,
        password: String
    ): Result<FirebaseUser?>
    suspend fun createUserInFirestore(user: User): Result<Void?>
    suspend fun loginUserInFirestore(email: String, password: String): Result<FirebaseUser?>
    fun logoutUserInFirestore()
    fun getUserInFirestore(): FirebaseUser?
}