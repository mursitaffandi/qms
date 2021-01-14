package com.citraweb.qms.data.queue

import com.citraweb.qms.data.user.User
import com.citraweb.qms.utils.Result
import com.google.firebase.auth.FirebaseUser

interface QueueRepository {
    suspend fun registerUserFromAuthWithEmailAndPassword(
        name: String,
        email: String,
        password: String
    ): Result<FirebaseUser?>
    suspend fun createUserInFirestore(user: User): Result<Void?>
    suspend fun loginUserInFirestore(email: String, password: String): Result<FirebaseUser?>
    fun logoutUserInFirestore()
    fun getUserInFirestore(): FirebaseUser?

    suspend fun getOpenQueue() :
    suspend fun joinQueue()
    suspend fun detailQueue()
    suspend fun exitQueue()

}