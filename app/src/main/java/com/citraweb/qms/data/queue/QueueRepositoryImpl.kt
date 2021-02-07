package com.citraweb.qms.data.queue

import com.citraweb.qms.MyApp
import com.citraweb.qms.data.department.Department
import com.citraweb.qms.utils.*
import com.citraweb.qms.utils.SharePrefManager.Companion.ID_USER
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import timber.log.Timber


class QueueRepositoryImpl : QueueRepository {
    private val prefManager = SharePrefManager(MyApp.instance)
    private val firestore = Firebase.firestore
    private val db = firestore.collection(DEPARTMENT_COLLECTION_NAME)

    override fun getQueryDepartment(): FirestoreRecyclerOptions<Department?> {
        return FirestoreRecyclerOptions.Builder<Department>()
                .setQuery(
                        db.whereEqualTo(
                                DEPARTMENT_STATUS,
                                StateDepartment.OPEN.name),
                        Department::class.java).build()
    }

    override suspend fun getQueues(): Result<List<Queue>?> {

        try {
            return when (val process = db.get().await()) {
                is Result.Success -> {
                    Result.Success(process.data.toObjects(Queue::class.java))
                }
                is Result.Error -> {
                    Timber.e("${process.exception}")
                    Result.Error(process.exception)
                }
                is Result.Canceled -> {
                    Timber.e("${process.exception}")
                    Result.Canceled(process.exception)
                }
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    override suspend fun addQueue(queue: Queue): Result<Void?> {
        /*val city = hashMapOf(
                "name" to "Los Angeles",
                "state" to "CA",
                "country" to "USA"
        )

        db.collection("cities").document("LA")
                .set(city)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }*/
        TODO("Not yet implemented")
    }

    override suspend fun joinQueue(idQueue: String, lastNumber : Int): Result<Void?> {
        val batch = firestore.batch()
            batch.update(
                    db.document(idQueue),
                            mapOf(
                            DEPARTMENT_WAITINGS to FieldValue.arrayUnion(prefManager.getFromPreference(ID_USER)),
                            DEPARTMENT_UPDATEDAT to now()
                    )
            )

        batch.update(
                    firestore.collection(USER_COLLECTION_NAME).document(prefManager.getFromPreference(ID_USER)),
                            mapOf(
                            USER_TICKETPARENT to idQueue,
                            USER_TICKET to lastNumber+1
                    )
            )


        return batch.commit().await()
    }

    fun observeSurender() {
    }
}