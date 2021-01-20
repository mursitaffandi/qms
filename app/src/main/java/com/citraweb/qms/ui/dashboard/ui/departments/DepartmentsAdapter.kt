package com.citraweb.qms.ui.dashboard.ui.departments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.citraweb.qms.MyApp
import com.citraweb.qms.R
import com.citraweb.qms.data.queue.Queue
import com.citraweb.qms.databinding.ItemDepartmentBinding

class DepartmentsAdapter(private val callback : OnItemDepartmentClick) : RecyclerView.Adapter<DepartmentsAdapter.DepartmentsViewHolder>() {
    private var queues: List<Queue>? = mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentsViewHolder {
        val binding = ItemDepartmentBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return DepartmentsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DepartmentsViewHolder, position: Int) {
        with(holder){
            with(queues?.get(position)) {
                binding.tvCompany.text = this?.companyId
                binding.tvDepartment.text = this?.name
                binding.tvWaiting.text = this?.waiting.toString()
                this?.isOpen?.let {
                    binding.tvStatue.text =  if(it) MyApp.instance.getString(R.string.open) else MyApp.instance.getString(R.string.close)
                }

                holder.itemView.setOnClickListener {
                    this?.let { it1 -> callback.click(it1) }
                }
            }
        }
    }

    inner class DepartmentsViewHolder(val binding: ItemDepartmentBinding)
        :RecyclerView.ViewHolder(binding.root)

    override fun getItemCount() = queues?.size ?: 0


    fun setData(success: List<Queue>?) {
        queues = success
        notifyDataSetChanged()
    }
}

fun interface OnItemDepartmentClick{
    fun click(Q : Queue)
}