package com.citraweb.qms.ui.dashboard.ui.queue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.citraweb.qms.R
import com.citraweb.qms.databinding.FragmentMyqueueBinding
import com.citraweb.qms.databinding.FragmentStaffBinding
import com.citraweb.qms.ui.MyViewModelFactory
import com.citraweb.qms.ui.dashboard.ui.staff.StaffViewModel

class MyTicketFragment : Fragment(), FireQueueAdapter.OnItemClick {

    private var binding: FragmentMyqueueBinding? = null
    private lateinit var adapter: FireQueueAdapter
    private lateinit var viewModel: MyTicketViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentMyqueueBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        viewModel = ViewModelProvider(this, MyViewModelFactory()).get(MyTicketViewModel::class.java)
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = FireQueueAdapter(viewModel.query, this)

        binding?.rvQueuesTicket?.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = this@MyTicketFragment.adapter
        }

        adapter.notifyDataSetChanged()

    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun size(itemCount: Int) {

    }
}