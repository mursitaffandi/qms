package com.citraweb.qms.repository.implementation

import com.citraweb.qms.data.model.User
import com.citraweb.qms.repository.UserRepository
import com.citraweb.qms.utils.Result
import com.citraweb.qms.utils.USER_COLLECTION_NAME
import com.citraweb.qms.utils.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber

class UserRepositoryImpl : UserRepository {
    private val firestoreInstance = FirebaseFirestore.getInstance()
    private val userCollection = firestoreInstance.collection(USER_COLLECTION_NAME)
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
            userCollection.document(user.id!!).set(user).await()
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
    }

    override fun getUserInFirestore(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
}