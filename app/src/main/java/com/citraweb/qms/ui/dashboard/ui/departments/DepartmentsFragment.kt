package com.citraweb.qms.ui.dashboard.ui.departments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.citraweb.qms.databinding.FragmentDeparmentsBinding
import com.citraweb.qms.ui.MyViewModelFactory

class DepartmentsFragment : Fragment() {

    private var binding: FragmentDeparmentsBinding? = null
    private lateinit var departmentsViewModel: DepartmentsViewModel
    private lateinit var adapter : DepartmentsAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentDeparmentsBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        departmentsViewModel = ViewModelProvider(this, MyViewModelFactory())
                .get(DepartmentsViewModel::class.java)
        adapter = DepartmentsAdapter {
            departmentsViewModel.join(it)
        }
        binding?.rvQueues?.apply {
            layoutManager = LinearLayoutManager(container?.context)
            adapter = this@DepartmentsFragment.adapter
        }
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        departmentsViewModel.getDepartments()
        departmentsViewModel.currentDepartmentsLD.observe(viewLifecycleOwner, Observer {
            val data = it ?: return@Observer
            adapter.setData(data.success)
        })
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}