package com.citraweb.qms.data.department

import com.citraweb.qms.MyApp
import com.citraweb.qms.data.queue.Queue
import com.citraweb.qms.data.user.User
import com.citraweb.qms.utils.*
import com.citraweb.qms.utils.SharePrefManager.Companion.ID_DEPARTMENT
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
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
    private val queueStore = db.collection(QUEUE_COLLECTION_NAME)

    override fun getQueryQueue(): FirestoreRecyclerOptions<Queue?> {
        return FirestoreRecyclerOptions.Builder<Queue>()
                .setQuery(
                        queueStore.whereEqualTo(
                                QUEUE_DEPARTMENT,
                                prefManager.getFromPreference(ID_DEPARTMENT)),
                        Queue::class.java).build()
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

    override suspend fun updateDepartement(action: StateDepartment, name: String, company: String): Result<Void?> {
        return try {
            if (action == StateDepartment.CLOSE) {
                when(val u = removeMember()){
                    is Result.Success -> {
                        departmentStore.document(prefManager.getFromPreference(ID_DEPARTMENT)).update(mapOf
                        (

                                DEPARTMENT_STATUS to action.name,
                                DEPARTMENT_NAME to name,
                                DEPARTMENT_COMPANYID to company,
                                DEPARTMENT_WAITINGS to null,
                                DEPARTMENT_UPDATEDAT to Timestamp.now()
                        )
                        ).await()
                    }
                    is Result.Error -> {Result.Error(u.exception)}
                    is Result.Canceled -> {Result.Canceled(u.exception)}
                }

            } else
            departmentStore.document(prefManager.getFromPreference(ID_DEPARTMENT)).update(mapOf
            (
                    DEPARTMENT_STATUS to action.name,
                    DEPARTMENT_UPDATEDAT to Timestamp.now()
            )
            ).await()
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun nextQueue(newIndex: Int): Result<Void?> {
        return try {
            departmentStore.document(prefManager.getFromPreference(ID_DEPARTMENT)).update(mapOf
            (
                    DEPARTMENT_CURRENTQUEUE to newIndex,
                    DEPARTMENT_UPDATEDAT to Timestamp.now()
            )
            ).await()
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun removeMember(): Result<Void?> {
        return try {
            when (val members = queueStore.whereEqualTo(QUEUE_DEPARTMENT, prefManager.getFromPreference(ID_DEPARTMENT)).get().await()) {
                is Result.Success -> {
                    val batch = db.batch()
                    members.data.documents.forEach { docSnap ->
                        batch.delete(
                                queueStore.document(docSnap.id)
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
