package com.citraweb.qms.repository

import com.google.firebase.auth.FirebaseUser
import com.citraweb.qms.data.model.User
import com.citraweb.qms.utils.Result


interface UserRepository
{
    suspend fun registerUserFromAuthWithEmailAndPassword(email: String, password: String): Result<FirebaseUser?>
    suspend fun createUserInFirestore(user: User): Result<Void?>
}