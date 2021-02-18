package com.citraweb.qms.data.queue

import com.citraweb.qms.data.department.Department
import com.citraweb.qms.utils.Result
import com.firebase.ui.firestore.FirestoreRecyclerOptions

interface QueueRepository {
    suspend fun joinQueue(idDepartment: String, idUser: String, department: Department): Result<Void?>
    fun getQueryDepartment(): FirestoreRecyclerOptions<Department?>
    fun getQueryQueue(): FirestoreRecyclerOptions<Queue?>
}