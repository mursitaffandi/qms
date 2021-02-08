package com.citraweb.qms.ui.dashboard.ui.departments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.citraweb.qms.data.department.Department
import com.citraweb.qms.databinding.ItemDepartmentBinding
import com.citraweb.qms.ui.dashboard.ui.departments.FireDepartmentAdapter.NoteHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import timber.log.Timber


class FireDepartmentAdapter(options: FirestoreRecyclerOptions<Department?>, private val callback: OnItemClick) :
    FirestoreRecyclerAdapter<Department, NoteHolder>(options) {
    var ticketParent : String? = null
    override fun onBindViewHolder(holder: NoteHolder, position: Int, model: Department) {
        val idDocument = snapshots.getSnapshot(position).id
        ticketParent?.let { if (idDocument == ticketParent) holder.itemView.visibility = View.GONE }
        holder.binding.tvCompany.text = model.companyId
        holder.binding.tvDepartment.text = model.name
        holder.binding.tvWaiting.text = model.prefix.toString()
        holder.binding.root.setOnClickListener {
            callback.click(model, snapshots.getSnapshot(position).id)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val binding = ItemDepartmentBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteHolder(binding)
    }

    override fun getItemCount(): Int {
        callback.size(super.getItemCount())
        return super.getItemCount()
    }

    override fun onError(e: FirebaseFirestoreException) {
        Timber.tag("FirestoreException").e(e);
        super.onError(e)
    }

    inner class NoteHolder(val binding: ItemDepartmentBinding)
        :RecyclerView.ViewHolder(binding.root)

    interface OnItemClick{
        fun click(S: Department, id: String)
        fun size(itemCount: Int)
    }
}

