package com.citraweb.qms.repository.implementation

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.citraweb.qms.utils.await
import com.citraweb.qms.data.model.User
import com.citraweb.qms.utils.USER_COLLECTION_NAME
import com.citraweb.qms.repository.UserRepository
import com.citraweb.qms.utils.Result
import timber.log.Timber

class UserRepositoryImpl : UserRepository
{
    private val firestoreInstance = FirebaseFirestore.getInstance()
    private val userCollection = firestoreInstance.collection(USER_COLLECTION_NAME)
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override suspend fun registerUserFromAuthWithEmailAndPassword(email: String, password: String): Result<FirebaseUser?>
    {
        try
        {
            return when(val resultDocumentSnapshot = firebaseAuth.createUserWithEmailAndPassword(email, password).await())
            {
                is Result.Success -> {
                    Timber.i("Result.Success")
                    val firebaseUser = resultDocumentSnapshot.data.user
                    Result.Success(firebaseUser)
                }
                is Result.Error -> {
                    Timber.e("${resultDocumentSnapshot.exception}")
                    Result.Error(resultDocumentSnapshot.exception)
                }
                is Result.Canceled ->  {
                    Timber.e("${resultDocumentSnapshot.exception}")
                    Result.Canceled(resultDocumentSnapshot.exception)
                }
            }
        }
        catch (exception: Exception)
        {
            return Result.Error(exception)
        }
    }

    override suspend fun createUserInFirestore(user: User): Result<Void?>
    {
        return try
        {
            userCollection.document(user.id).set(user).await()
        }
        catch (exception: Exception)
        {
            Result.Error(exception)
        }
    }
}