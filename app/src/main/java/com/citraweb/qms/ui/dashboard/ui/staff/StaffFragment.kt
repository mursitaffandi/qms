package com.citraweb.qms.ui.dashboard.ui.staff

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.citraweb.qms.data.department.Department
import com.citraweb.qms.databinding.FragmentStaffBinding
import com.citraweb.qms.ui.MyViewModelFactory
import com.citraweb.qms.utils.SURRENDER_COLLECTION_NAME
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class StaffFragment : Fragment(), FireAdapter.OnItemClick {

    private var binding: FragmentStaffBinding? = null
    private lateinit var viewModel: StaffViewModel
    private lateinit var adapter: FireAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentStaffBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        viewModel = ViewModelProvider(this, MyViewModelFactory())
                .get(StaffViewModel::class.java)

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val response = FirestoreRecyclerOptions.Builder<Department>()
                .setQuery(surrender.whereEqualTo("status", 1), Department::class.java)
                .build()

        adapter = FireAdapter(response, this)

        binding?.rvStaff?.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = this@StaffFragment.adapter
        }

        adapter.notifyDataSetChanged()

        binding?.ivPower?.setOnLongClickListener {
            powerHandler(it as ImageView)
        }

        binding?.ivPower?.setOnClickListener {
            viewModel.powerClick()

        }
    }

    private fun powerHandler(it: ImageView): Boolean {
        TODO("Not yet implemented")
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

    override fun click(S: Department, id: String) {
//            TODO : add action item click
        surrender.document(id).set(Department("digitobe", ""))
    }

    override fun size(itemCount: Int) {
        binding?.staffSlider?.isLocked = itemCount > 0
    }
}