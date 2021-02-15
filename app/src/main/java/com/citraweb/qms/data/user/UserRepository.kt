package com.citraweb.qms.data.user

import com.citraweb.qms.utils.Result
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference


interface UserRepository
{
    suspend fun registerUserFromAuthWithEmailAndPassword(
            name: kotlin.String,
            email: kotlin.String,
            password: kotlin.String
    ): Result<FirebaseUser?>
    suspend fun createUserInFirestore(id: kotlin.String, user: User): Result<Void?>
    suspend fun createDepartmnetInFirestore(staffId : kotlin.String): Result<DocumentReference?>
    suspend fun loginUserInFirestore(email: kotlin.String, password: kotlin.String): Result<FirebaseUser?>
    fun logoutUserInFirestore()
    suspend fun getUserInFirestore(): Result<User?>
    fun setDepartmentId(it: kotlin.String)
    suspend fun updateFcmToken(newToken: kotlin.String): Result<Void?>
}