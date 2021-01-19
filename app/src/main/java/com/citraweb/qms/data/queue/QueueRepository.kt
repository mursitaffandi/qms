package com.citraweb.qms.data.queue

import com.citraweb.qms.data.user.User
import com.citraweb.qms.utils.Result
import com.google.firebase.auth.FirebaseUser

interface QueueRepository {
    suspend fun getOpenQueue() : Result<List<Queue>?>
    suspend fun joinQueue(idQueue : String, idMember : String) : Result<Void?>
}