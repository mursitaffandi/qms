package com.citraweb.qms.data.queue

import com.citraweb.qms.data.department.Department
import com.citraweb.qms.utils.Result
import com.firebase.ui.firestore.FirestoreRecyclerOptions

interface QueueRepository {
    suspend fun joinQueue(idQueue : String, lastNumber : Int) : Result<Void?>
    fun getQueryDepartment(): FirestoreRecyclerOptions<Department?>
}