package com.citraweb.qms.ui.dashboard.ui.queue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.citraweb.qms.data.queue.QueueRepository
import com.citraweb.qms.utils.MyBaseViewModel

class MyTicketViewModel constructor(private val repository: QueueRepository) : MyBaseViewModel() {

    val query = repository.getQueryQueue()

    private val _text = MutableLiveData<String>().apply {
        value = "This is slideshow Fragment"
    }
    val text: LiveData<String> = _text
}