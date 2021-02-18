package com.citraweb.qms.ui.dashboard.ui.departments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.citraweb.qms.data.department.Department
import com.citraweb.qms.databinding.FragmentDeparmentsBinding
import com.citraweb.qms.ui.MyViewModelFactory
import com.citraweb.qms.utils.SharePrefManager
import com.citraweb.qms.utils.SharePrefManager.Companion.ID_USER

class DepartmentsFragment : Fragment(), FireDepartmentAdapter.OnItemClick {

    private var binding: FragmentDeparmentsBinding? = null
    private lateinit var viewModel: DepartmentsViewModel
    private lateinit var adapter: FireDepartmentAdapter
    private lateinit var prefManager: SharePrefManager
    private lateinit var idUser : String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentDeparmentsBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        viewModel = ViewModelProvider(this, MyViewModelFactory()).get(DepartmentsViewModel::class.java)
        prefManager = SharePrefManager(requireActivity())
        idUser = prefManager.getFromPreference(ID_USER)
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = FireDepartmentAdapter(viewModel.query, this, idUser)
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

    override fun click(departement: Department, id: String) {
        viewModel.join(id, idUser, departement)
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