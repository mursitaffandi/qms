package com.citraweb.qms.ui.dashboard.ui.departments

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.citraweb.qms.data.queue.Queue

class DepartmentsAdapter : RecyclerView.Adapter<DepartmentsAdapter.DepartmentsViewHolder>() {
    private var queues: List<Queue>? = mutableListOf()

    class DepartmentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentsViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: DepartmentsViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return queues?.size ?: 0
    }

    fun setData(success: List<Queue>?) {
        queues = success
    }
}