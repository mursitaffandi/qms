package com.citraweb.qms.ui.dashboard.ui.queue

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.citraweb.qms.data.queue.Queue
import com.citraweb.qms.databinding.ItemQueueBinding
import com.citraweb.qms.utils.convertTime
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import timber.log.Timber


class FireQueueAdapter(
        options: FirestoreRecyclerOptions<Queue?>,
        private val callback: OnItemClick
) :
    FirestoreRecyclerAdapter<Queue, FireQueueAdapter.QueueHolder>(options) {
    override fun onBindViewHolder(holder: QueueHolder, position: Int, model: Queue) {
        holder.binding.tvTicketDate.text = model.createdAt?.let { convertTime(it) }
        holder.binding.tvTicketNumber.text = "${model.prefix}-${model.ticket}"
        holder.binding.tvTicketCompany.text = model.departmentCompany
        holder.binding.tvTicketDepartment.text = model.departmentName
        holder.binding.tvTicketWaiting.text = "Jumlah Menunggu : ${model.waiting.toString()}"
        holder.binding.tvTicket.text = model.status
        holder.itemView.setOnClickListener {
            callback.click(model.user, model.department)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueHolder {
        val binding = ItemQueueBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QueueHolder(binding)
    }

    override fun getItemCount(): Int {
        callback.size(super.getItemCount())
        return super.getItemCount()
    }

    override fun onError(e: FirebaseFirestoreException) {
        Timber.tag("FireStoreException").e(e)
        super.onError(e)
    }

    inner class QueueHolder(val binding: ItemQueueBinding) :RecyclerView.ViewHolder(binding.root)

    interface OnItemClick{
        fun size(itemCount: Int)
        fun click(idUser : String?, departmentId : String?){}
    }
}

