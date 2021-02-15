package com.citraweb.qms.data.queue

import com.citraweb.qms.MyApp
import com.citraweb.qms.data.department.Department
import com.citraweb.qms.data.user.User
import com.citraweb.qms.utils.*
import com.citraweb.qms.utils.SharePrefManager.Companion.ID_USER
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


class QueueRepositoryImpl : QueueRepository {
    private val prefManager = SharePrefManager(MyApp.instance)
    private val firestore = Firebase.firestore
    private val departments = firestore.collection(DEPARTMENT_COLLECTION_NAME)
    private val users = firestore.collection(USER_COLLECTION_NAME)
    private val queues = firestore.collection(QUEUE_COLLECTION_NAME)

    override fun getQueryQueue(): FirestoreRecyclerOptions<Queue?> {
        return FirestoreRecyclerOptions.Builder<Queue>()
                .setQuery(
                        queues.whereEqualTo(
                                QUEUE_USER,
                                prefManager.getFromPreference(ID_USER)),
                        Queue::class.java).build()
    }


    override fun getQueryDepartment(): FirestoreRecyclerOptions<Department?> {
        return FirestoreRecyclerOptions
                .Builder<Department>()
                .setQuery(departments
                        .whereEqualTo(
                        DEPARTMENT_STATUS,
                        StateDepartment.OPEN.name)
                        .whereNotEqualTo(
                                DEPARTMENT_STAFFID,
                                prefManager.getFromPreference(ID_USER)
                        ),
                        Department::class.java).build()
    }

    @ExperimentalCoroutinesApi
    override suspend fun detailUser(): Flow<Result<User?>> = callbackFlow {
        val subscription = users.document(prefManager.getFromPreference(ID_USER)).addSnapshotListener { value, error ->
            if (value!!.exists()) {
                val detailDepartment = value.toObject(User::class.java)
                offer(Result.Success(detailDepartment))
            } else {
                offer(Result.Error(error!!))
            }
        }
        awaitClose { subscription.remove() }
    }

    override suspend fun joinQueue(idQueue: String, lastNumber: Int): Result<Void?> {
        val batch = firestore.batch()
        batch.update(
                departments.document(idQueue),
                mapOf(
                        DEPARTMENT_WAITINGS to FieldValue.arrayUnion(prefManager.getFromPreference(ID_USER)),
                        DEPARTMENT_AMOUNT to lastNumber + 1,
                        DEPARTMENT_UPDATEDAT to now()
                )
        )

        batch.update(
                users.document(prefManager.getFromPreference(ID_USER)),
                mapOf(
                        USER_TICKETPARENT to idQueue,
                        USER_TICKET to lastNumber + 1
                )
        )

        return batch.commit().await()
    }
}