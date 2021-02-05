package com.citraweb.qms.data.department

import com.citraweb.qms.MyApp
import com.citraweb.qms.data.user.User
import com.citraweb.qms.utils.*
import com.citraweb.qms.utils.SharePrefManager.Companion.ID_DEPARTMENT
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@ExperimentalCoroutinesApi
class StaffRepositoryImpl : StaffAction {
    private val prefManager = SharePrefManager(MyApp.instance)
    private val db = Firebase.firestore
    private val departmentStore = db.collection(DEPARTMENT_COLLECTION_NAME)
    private val userStore = db.collection(USER_COLLECTION_NAME)

    override fun getQueryQueue(): FirestoreRecyclerOptions<User?> {
        return FirestoreRecyclerOptions.Builder<User>()
                .setQuery(
                        userStore.whereEqualTo(
                                USER_TICKETPARENT,
                                prefManager.getFromPreference(ID_DEPARTMENT)),
                        User::class.java).build()
    }

    override suspend fun detailDepartment(): Flow<Result<Department?>> = callbackFlow {
        val subscription = departmentStore.document(prefManager.getFromPreference(ID_DEPARTMENT)).addSnapshotListener { value, error ->
            if (value!!.exists()) {
                val detailDepartment = value.toObject(Department::class.java)
                offer(Result.Success(detailDepartment))
            } else {
                offer(Result.Error(error!!))
            }
        }

        awaitClose { subscription.remove() }

    }

    override suspend fun power(action: StateDepartment): Result<Void?> {
        return try {
            if (action == StateDepartment.CLOSE) {
                when(val u = removeMember()){
                    is Result.Success -> {
                        departmentStore.document(prefManager.getFromPreference(ID_DEPARTMENT)).update(mapOf
                        (
                                DEPARTMENT_STATUS to action.name,
                                DEPARTMENT_WAITINGS to null
                        )
                        ).await()
                    }
                    is Result.Error -> {Result.Error(u.exception)}
                    is Result.Canceled -> {Result.Canceled(u.exception)}
                }

            } else
            departmentStore.document(prefManager.getFromPreference(ID_DEPARTMENT)).update(DEPARTMENT_STATUS, action.name).await()
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun nextQueue(newIndex: Int): Result<Void?> {
        return try {
            departmentStore.document(prefManager.getFromPreference(ID_DEPARTMENT)).update(DEPARTMENT_CURRENTQUEUE, newIndex).await()
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun removeMember(): Result<Void?> {
        return try {
            when (val members = userStore.whereEqualTo(USER_TICKETPARENT, prefManager.getFromPreference(ID_DEPARTMENT)).get().await()) {
                is Result.Success -> {
                    val batch = db.batch()
                    members.data.documents.forEach { docSnap ->
                        batch.update(
                                userStore.document(docSnap.id),
                                USER_TICKETPARENT,
                                null
                        )
                    }
                    batch.commit().await()
                }
                is Result.Error -> {
                    Result.Error(members.exception)

                }
                is Result.Canceled -> {
                    Result.Canceled(members.exception)
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

}
