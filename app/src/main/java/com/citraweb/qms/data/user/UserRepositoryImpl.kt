package com.citraweb.qms.data.user

import com.citraweb.qms.MyApp
import com.citraweb.qms.data.department.Department
import com.citraweb.qms.utils.*
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import timber.log.Timber
import java.time.LocalDateTime.now

class UserRepositoryImpl : UserRepository {
    private val userCollection = Firebase.firestore.collection(USER_COLLECTION_NAME)
    private val departmentCollection = Firebase.firestore.collection(DEPARTMENT_COLLECTION_NAME)
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override suspend fun registerUserFromAuthWithEmailAndPassword(
        name: String,
        email: String,
        password: String
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

    override suspend fun createUserInFirestore(user: User): Result<Void?> {
        return try {
            userCollection.document(user.userId!!).set(user).await()
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override suspend fun createDepartmnetInFirestore(user: User): Result<DocumentReference?> {
        return try {
            departmentCollection.add(
                Department(
                    companyId = "companyMboh",
                    departmentId = "iogn34oing23",
                    name = "Police Department",
                    prefix = "P",
                    staffId = user.userId,
                    status = StateDepartment.CLOSE.name,
                )
            ).await()
        } catch (exception: Exception) {
            Result.Error(exception)
        }

    }

    override suspend fun loginUserInFirestore(
        email: String,
        password: String
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
        MyApp.idDocumentDepartment = null
        MyApp.idDocumentUser = null
    }

    override suspend fun getUserInFirestore(): Result<User?> {
        try {
            return when (val resultDocumentSnapshot =
                userCollection.document(firebaseAuth.uid!!).get().await()) {
                is Result.Success -> {
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


}