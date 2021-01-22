package com.citraweb.qms.ui.dashboard.ui.staff

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.citraweb.qms.data.department.Surrender
import com.citraweb.qms.data.queue.Queue
import com.citraweb.qms.databinding.ItemDepartmentBinding
import com.citraweb.qms.ui.dashboard.ui.staff.FireAdapter.NoteHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import timber.log.Timber

class FireAdapter(options: FirestoreRecyclerOptions<Surrender?>, private val callback : OnItemClick) :
    FirestoreRecyclerAdapter<Surrender, NoteHolder>(options) {
    override fun onBindViewHolder(holder: NoteHolder, position: Int, model: Surrender) {
        holder.binding.tvDepartment.text = model.name
        holder.binding.tvWaiting.text = model.clazz.toString()
        holder.binding.root.setOnClickListener {
            callback.click(model)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val binding = ItemDepartmentBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteHolder(binding)
    }

    override fun onError(e: FirebaseFirestoreException) {
        Timber.tag("FirestoreException").e(e);
        super.onError(e)
    }

    inner class NoteHolder(val binding: ItemDepartmentBinding)
        :RecyclerView.ViewHolder(binding.root)

    fun interface OnItemClick{
        fun click(S : Surrender)
    }
}

