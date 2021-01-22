package com.citraweb.qms.data.queue

import android.util.Log
import com.citraweb.qms.utils.DEPARTMENT_COLLECTION_NAME
import com.citraweb.qms.utils.Result
import com.citraweb.qms.utils.await
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import timber.log.Timber


class QueueRepositoryImpl : QueueRepository {
    private val db = Firebase.firestore.collection(DEPARTMENT_COLLECTION_NAME)
    override suspend fun getQueues(): Result<List<Queue>?> {

        try {
            return when (val process = db.get().await()) {
                is Result.Success -> {
                    var myColl : MutableList<Queue> = ArrayList()
                    Log.d("JEMBUT", process.data.documents[0].get("company_id").toString())
                    for (doc in process.data.documents) {
                        val a = doc.toObject(Queue::class.java)
                        if (a != null)
                            myColl.add(a)
                    }
                    Result.Success(myColl)
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

    override suspend fun joinQueue(idQueue: String, idMember: String): Result<Void?> {
        TODO("Not yet implemented")
    }


}