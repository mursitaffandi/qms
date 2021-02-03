package com.citraweb.qms.data.department

import com.citraweb.qms.data.user.User
import com.citraweb.qms.utils.Result
import com.citraweb.qms.utils.StateDepartment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.coroutines.flow.Flow


interface StaffAction {
    fun getQueryQueue() : FirestoreRecyclerOptions<User?>
    suspend fun detailDepartment() : Flow<Result<Department?>>
    suspend fun power(action : StateDepartment) : Result<Void?>
}