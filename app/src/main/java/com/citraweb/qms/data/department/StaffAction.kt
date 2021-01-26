package com.citraweb.qms.data.department

import com.citraweb.qms.utils.Result
import com.firebase.ui.firestore.FirestoreRecyclerOptions


interface StaffAction {
    fun getQuery(departmentId : String) : FirestoreRecyclerOptions<Department>
    suspend fun detailDepartment() : Result<Department?>
    suspend fun open() : Result<Void?>
    suspend fun close() : Result<Void?>
}