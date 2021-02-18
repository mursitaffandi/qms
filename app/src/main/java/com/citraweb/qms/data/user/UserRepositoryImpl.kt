package com.citraweb.qms.data.user

import com.citraweb.qms.MyApp
import com.citraweb.qms.data.department.Department
import com.citraweb.qms.utils.*
import com.citraweb.qms.utils.SharePrefManager.Companion.ID_USER
import com.google.common.base.Throwables
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class UserRepositoryImpl : UserRepository {
    private val departmentCollection = Firebase.firestore.collection(DEPARTMENT_COLLECTION_NAME)
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userCollection = Firebase.firestore.collection(USER_COLLECTION_NAME)
    private val prefManager = SharePrefManager(MyApp.instance)

    override fun deleteAll() {
        val listCollection = listOf(
                USER_COLLECTION_NAME,
                DEPARTMENT_COLLECTION_NAME,
                QUEUE_COLLECTION_NAME
        )
        listCollection.forEach {
            purgeAllData(it)
        }
    }

     fun purgeAllData(collection: String) {
        Firebase.firestore.collection(collection).get().addOnCompleteListener { task ->
            val batch = Firebase.firestore.batch()

            task.result?.documents?.forEach {
                batch.delete(Firebase.firestore.collection(collection).document(it.id))
            }
            batch.commit().addOnCompleteListener {

            }
        }

    }

    override suspend fun updateFcmToken(newToken: String): Result<Void?> {
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

    override suspend fun sendPasswordResetEmail(email: String): Result<Void?> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }


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

    override suspend fun createUserInFirestore(id: String, user: User): Result<Void?> {
        return try {
            userCollection.document(id).set(user).await()
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override suspend fun createDepartmnetInFirestore(staffId: String): Result<DocumentReference?> {
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
        prefManager.clearAll()
    }

    override suspend fun getUserInFirestore(): Result<User?> {
        firebaseAuth.uid?.let {
            prefManager.setUserId(it)
            try {
                return when (val resultDocumentSnapshot = userCollection.document(it).get().await()) {
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
        return Result.Error(Exception("user not found"))
    }

    override fun setDepartmentId(it: String) {
        prefManager.setDepartmentId(it)
    }

}