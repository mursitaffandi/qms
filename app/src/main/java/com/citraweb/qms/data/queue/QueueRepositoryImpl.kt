package com.citraweb.qms.data.queue

import com.citraweb.qms.MyApp
import com.citraweb.qms.data.department.Department
import com.citraweb.qms.utils.*
import com.citraweb.qms.utils.SharePrefManager.Companion.ID_USER
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class QueueRepositoryImpl : QueueRepository {
    private val prefManager = SharePrefManager(MyApp.instance)
    private val firestore = Firebase.firestore
    private val departments = firestore.collection(DEPARTMENT_COLLECTION_NAME)
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

    override suspend fun joinQueue(idDepartment: String, idUser: String, department: Department): Result<Void?> {
        val batch = firestore.batch()
        val ticket = department.amount?:0 + 1
        var waiting = 0
        department.waitings?.let {
            waiting = it.size - department.currentQueue
        }
        batch.update(
                departments.document(idDepartment),
                mapOf(
                        DEPARTMENT_WAITINGS to FieldValue.arrayUnion(prefManager.getFromPreference(ID_USER)),
                        DEPARTMENT_AMOUNT to ticket,
                        DEPARTMENT_UPDATEDAT to now()
                )
        )

        batch.set(
                queues.document(),
                Queue(
                        department = idDepartment,
                        status = StateQueue.WAITING.name,
                        ticket = ticket,
                        prefix = department.prefix,
                        user = idUser,
                        waiting = waiting
                )
        )

        return batch.commit().await()
    }
}