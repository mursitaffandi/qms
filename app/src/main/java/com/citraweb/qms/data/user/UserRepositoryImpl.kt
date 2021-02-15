package com.citraweb.qms.data.user

import com.citraweb.qms.MyApp
import com.citraweb.qms.data.department.Department
import com.citraweb.qms.utils.*
import com.citraweb.qms.utils.SharePrefManager.Companion.ID_USER
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class UserRepositoryImpl : UserRepository {
    private val departmentCollection = Firebase.firestore.collection(DEPARTMENT_COLLECTION_NAME)
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val userCollection = Firebase.firestore.collection(USER_COLLECTION_NAME)
    private val prefManager = SharePrefManager(MyApp.instance)

    override suspend fun updateFcmToken(newToken: kotlin.String): Result<Void?> {
        return try {
            when (val update =
                userCollection.document(prefManager.getFromPreference(ID_USER)).update(
                    USER_FCM, newToken
                ).await()) {
                is Result.Success -> {
                    Result.Success(update.data)
                }
                is Result.Error -> {
                    Result.Error(update.exception)
                }
                is Result.Canceled -> {
                    Result.Canceled(update.exception)
                }
            }
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }


    override suspend fun registerUserFromAuthWithEmailAndPassword(
            name: kotlin.String,
            email: kotlin.String,
            password: kotlin.String
    ): Result<FirebaseUser?> {
        try {
            return when (val resultDocumentSnapshot =
                firebaseAuth.createUserWithEmailAndPassword(email, password).await()) {
                is Result.Success -> {
                    Timber.i("Result.Success")
                    val firebaseUser = resultDocumentSnapshot.data.user
                    /*try {
                        when(firebaseUser?.updateProfile(userProfileChangeRequest{ displayName = name })?.await()){
                            is Result.Success -> Result.Success(firebaseUser)
                            is Result.Error -> Result.Success(firebaseUser)
                            is Result.Canceled -> Result.Success(firebaseUser)
                            null -> Result.Success(firebaseUser)
                        }
                    } catch (exception: Exception)
                    {
                        return Result.Error(exception)
                    }*/
                    firebaseUser?.updateProfile(userProfileChangeRequest { displayName = name })
                    Result.Success(firebaseUser)
                }
                is Result.Error -> {
                    Timber.e("${resultDocumentSnapshot.exception}")
                    Result.Error(resultDocumentSnapshot.exception)
                }
                is Result.Canceled -> {
                    Timber.e("${resultDocumentSnapshot.exception}")
                    Result.Canceled(resultDocumentSnapshot.exception)
                }
            }
        } catch (exception: Exception) {
            return Result.Error(exception)
        }
    }

    override suspend fun createUserInFirestore(id: kotlin.String, user: User): Result<Void?> {
        return try {
            userCollection.document(id).set(user).await()
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override suspend fun createDepartmnetInFirestore(staffId: kotlin.String): Result<DocumentReference?> {
        return try {
            departmentCollection.add(
                    Department(
                            companyId = "",
                            name = "",
                            prefix = "",
                            staffId = staffId,
                            status = StateDepartment.CLOSE.name,
                    )
            ).await()
        } catch (exception: Exception) {
            Result.Error(exception)
        }

    }

    override suspend fun loginUserInFirestore(
            email: kotlin.String,
            password: kotlin.String
    ): Result<FirebaseUser?> {
        try {
            return when (val resultDocumentSnapshot =
                firebaseAuth.signInWithEmailAndPassword(email, password).await()) {
                is Result.Success -> {
                    Timber.i("Result.Success")
                    val firebaseUser = resultDocumentSnapshot.data.user
                    Result.Success(firebaseUser)
                }
                is Result.Error -> {
                    Timber.e("${resultDocumentSnapshot.exception}")
                    Result.Error(resultDocumentSnapshot.exception)
                }
                is Result.Canceled -> {
                    Timber.e("${resultDocumentSnapshot.exception}")
                    Result.Canceled(resultDocumentSnapshot.exception)
                }
            }
        } catch (exception: Exception) {
            return Result.Error(exception)
        }
    }

    override fun logoutUserInFirestore() {
        firebaseAuth.signOut()
        prefManager.clearAll()
    }

    override suspend fun getUserInFirestore(): Result<User?> {
        try {
            return when (val resultDocumentSnapshot =
                userCollection.document(firebaseAuth.uid!!).get().await()) {
                is Result.Success -> {
                    prefManager.setUserId(firebaseAuth.uid!!)
                    Result.Success(resultDocumentSnapshot.data.toObject(User::class.java))
                }
                is Result.Error -> {
                    Timber.e("${resultDocumentSnapshot.exception}")
                    Result.Error(resultDocumentSnapshot.exception)
                }
                is Result.Canceled -> {
                    Timber.e("${resultDocumentSnapshot.exception}")
                    Result.Canceled(resultDocumentSnapshot.exception)
                }
            }
        } catch (exception: Exception) {
            return Result.Error(exception)
        }
    }

    override fun setDepartmentId(it: kotlin.String) {
        prefManager.setDepartmentId(it)
    }

}