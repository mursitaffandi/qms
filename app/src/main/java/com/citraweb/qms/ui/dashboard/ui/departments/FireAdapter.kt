package com.citraweb.qms.ui.dashboard.ui.departments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.citraweb.qms.R
import com.citraweb.qms.data.department.Department
import com.citraweb.qms.ui.dashboard.ui.departments.FireAdapter.NoteHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class FireAdapter(options: FirestoreRecyclerOptions<Department?>) :
    FirestoreRecyclerAdapter<Department, NoteHolder>(options) {
    override fun onBindViewHolder(holder: NoteHolder, position: Int, model: Department) {
snapshots.getSnapshot(position).id
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.item_department,
            parent, false
        )
        return NoteHolder(v)
    }

    inner class NoteHolder(itemView: View?) : RecyclerView.ViewHolder(
        itemView!!
    )
}