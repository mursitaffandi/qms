package com.citraweb.qms.ui.dashboard.ui.queue

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.citraweb.qms.data.queue.Queue
import com.citraweb.qms.databinding.ItemQueueBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import timber.log.Timber


class FireQueueAdapter(
        options: FirestoreRecyclerOptions<Queue?>,
        private val callback: OnItemClick
) :
    FirestoreRecyclerAdapter<Queue, FireQueueAdapter.QueueHolder>(options) {
    var ticketParent : String? = null
    override fun onBindViewHolder(holder: QueueHolder, position: Int, model: Queue) {
        val idDocument = snapshots.getSnapshot(position).id

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueHolder {
        val binding = ItemQueueBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return QueueHolder(binding)
    }

    override fun getItemCount(): Int {
        callback.size(super.getItemCount())
        return super.getItemCount()
    }

    override fun onError(e: FirebaseFirestoreException) {
        Timber.tag("FirestoreException").e(e);
        super.onError(e)
    }

    inner class QueueHolder(val binding: ItemQueueBinding)
        :RecyclerView.ViewHolder(binding.root)

    interface OnItemClick{
        fun size(itemCount: Int)
    }
}

