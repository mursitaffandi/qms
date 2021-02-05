package com.citraweb.qms.data.queue

import com.citraweb.qms.utils.Result

interface QueueRepository {
    suspend fun getQueues() : Result<List<Queue>?>
    suspend fun addQueue(queue: Queue) : Result<Void?>
    suspend fun joinQueue(idQueue : String, idMember : String) : Result<Void?>
}