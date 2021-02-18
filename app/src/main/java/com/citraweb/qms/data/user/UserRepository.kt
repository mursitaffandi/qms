package com.citraweb.qms.data.user

import com.citraweb.qms.utils.Result
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference


interface UserRepository
{
    suspend fun registerUserFromAuthWithEmailAndPassword(
            name: String,
            email: String,
            password: String
    ): Result<FirebaseUser?>
    suspend fun createUserInFirestore(id: String, user: User): Result<Void?>
    suspend fun createDepartmnetInFirestore(staffId : String): Result<DocumentReference?>
    suspend fun loginUserInFirestore(email: String, password: String): Result<FirebaseUser?>
    fun logoutUserInFirestore()
    suspend fun getUserInFirestore(): Result<User?>
    fun setDepartmentId(it: String)
    suspend fun updateFcmToken(newToken: String): Result<Void?>
    suspend fun sendPasswordResetEmail(email: String): Result<Void?>
    fun deleteAll()
}