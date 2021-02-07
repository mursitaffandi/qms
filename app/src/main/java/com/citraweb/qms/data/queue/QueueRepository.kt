package com.citraweb.qms.data.queue

import com.citraweb.qms.data.department.Department
import com.citraweb.qms.utils.Result
import com.firebase.ui.firestore.FirestoreRecyclerOptions

interface QueueRepository {
    suspend fun getQueues() : Result<List<Queue>?>
    suspend fun addQueue(queue: Queue) : Result<Void?>
    suspend fun joinQueue(idQueue : String) : Result<Void?>
    fun getQueryDepartment(): FirestoreRecyclerOptions<Department?>

}