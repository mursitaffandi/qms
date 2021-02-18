package com.citraweb.qms.ui.dashboard.ui.queue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.citraweb.qms.data.department.StaffRepositoryImpl
import com.citraweb.qms.data.queue.Queue
import com.citraweb.qms.data.queue.QueueRepository
import com.citraweb.qms.utils.MyBaseViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class MyTicketViewModel constructor(private val repository: QueueRepository) : MyBaseViewModel() {

    val query = repository.getQueryQueue()

    private val _text = MutableLiveData<String>().apply {
        value = "This is slideshow Fragment"
    }
    val text: LiveData<String> = _text
}