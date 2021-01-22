package com.citraweb.qms.ui.dashboard.ui.staff

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.citraweb.qms.data.department.Surrender
import com.citraweb.qms.databinding.FragmentStaffBinding
import com.citraweb.qms.ui.MyViewModelFactory
import com.citraweb.qms.ui.dashboard.ui.departments.DepartmentsViewModel
import com.citraweb.qms.utils.SURRENDER_COLLECTION_NAME
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class StaffFragment : Fragment() {

    private var binding: FragmentStaffBinding? = null
    private lateinit var departmentsViewModel: DepartmentsViewModel
    private lateinit var adapter : FireAdapter
    private val surrender = Firebase.firestore.collection(SURRENDER_COLLECTION_NAME)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentStaffBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        departmentsViewModel = ViewModelProvider(this, MyViewModelFactory())
            .get(DepartmentsViewModel::class.java)

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val response = FirestoreRecyclerOptions.Builder<Surrender>()
                .setQuery(surrender, Surrender::class.java)
                .build()

        adapter = FireAdapter(response) { s, id ->
//            TODO : add action item click
            surrender.document(id).set(Surrender("digitobe", 99))
        }
        binding?.rvStaff?.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = this@StaffFragment.adapter
        }

        adapter.notifyDataSetChanged()

//        binding?.example.se

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

}