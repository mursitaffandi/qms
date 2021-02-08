package com.citraweb.qms.ui.dashboard.ui.departments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.citraweb.qms.data.department.Department
import com.citraweb.qms.databinding.FragmentDeparmentsBinding
import com.citraweb.qms.ui.MyViewModelFactory
import timber.log.Timber

class DepartmentsFragment : Fragment(), FireDepartmentAdapter.OnItemClick {

    private var binding: FragmentDeparmentsBinding? = null
    private lateinit var departmentsViewModel: DepartmentsViewModel
    private lateinit var adapter : FireDepartmentAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentDeparmentsBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        departmentsViewModel = ViewModelProvider(this, MyViewModelFactory())
                .get(DepartmentsViewModel::class.java)

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FireDepartmentAdapter(departmentsViewModel.query, this)

        binding?.rvQueues?.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = this@DepartmentsFragment.adapter
        }

        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun click(S: Department, id: String) {
         departmentsViewModel.join(S.amount, id)
    }

    override fun size(itemCount: Int) {
        Log.d("itemCount", itemCount.toString())
    }
}