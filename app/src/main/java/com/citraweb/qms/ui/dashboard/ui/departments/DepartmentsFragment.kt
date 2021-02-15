package com.citraweb.qms.ui.dashboard.ui.departments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.citraweb.qms.MyApp
import com.citraweb.qms.data.department.Department
import com.citraweb.qms.databinding.FragmentDeparmentsBinding
import com.citraweb.qms.ui.MyViewModelFactory
import com.citraweb.qms.utils.Result
import com.citraweb.qms.utils.SharePrefManager
import com.citraweb.qms.utils.SharePrefManager.Companion.ID_USER

class DepartmentsFragment : Fragment(), FireDepartmentAdapter.OnItemClick {

    private var binding: FragmentDeparmentsBinding? = null
    private lateinit var departmentsViewModel: DepartmentsViewModel
    private lateinit var adapter: FireDepartmentAdapter
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
        adapter = FireDepartmentAdapter(departmentsViewModel.query, this, SharePrefManager(MyApp.instance).getFromPreference(ID_USER))
        binding?.rvQueues?.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = this@DepartmentsFragment.adapter
        }
        adapter.notifyDataSetChanged()

        departmentsViewModel.user.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Result.Success -> {
                    val user = result.data

                }
                is Result.Error -> {
                }
                is Result.Canceled -> {
                }
            }
        })
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun click(S: Department, id: String) {
        departmentsViewModel.join(S.amount, id)
    }

    override fun size(itemCount: Int) {
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

}