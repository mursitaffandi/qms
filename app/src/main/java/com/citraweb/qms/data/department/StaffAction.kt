package com.citraweb.qms.data.department

import com.citraweb.qms.data.queue.Queue
import com.citraweb.qms.utils.Result
import com.citraweb.qms.utils.StateDepartment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.coroutines.flow.Flow


interface StaffAction {
    fun getQueryQueue() : FirestoreRecyclerOptions<Queue?>
    suspend fun detailDepartment() : Flow<Result<Department?>>
    suspend fun updateDepartement(action: StateDepartment, name: String, company: String, prefix: String) : Result<Void?>
    suspend fun nextQueue(newIndex : Int) : Result<Void?>
    suspend fun removeMember() : Result<Void?>
    suspend fun updateWaiting()
    suspend fun getFcm(idUser: String): Result<String?>
    suspend fun updateQueueStatus(idUser: String) : Result<Void?>
}