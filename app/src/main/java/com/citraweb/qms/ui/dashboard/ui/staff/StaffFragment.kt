package com.citraweb.qms.ui.dashboard.ui.staff

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.citraweb.qms.data.user.User
import com.citraweb.qms.databinding.FragmentStaffBinding
import com.citraweb.qms.ui.MyViewModelFactory
import com.ncorti.slidetoact.SlideToActView


class StaffFragment : Fragment(), FireMemberAdapter.OnItemClick{

    private var binding: FragmentStaffBinding? = null
    private lateinit var viewModel: StaffViewModel
    private lateinit var memberAdapter: FireMemberAdapter

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

        memberAdapter = FireMemberAdapter(viewModel.query, this)

        binding?.rvStaff?.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = this@StaffFragment.memberAdapter
        }

        memberAdapter.notifyDataSetChanged()

        binding?.ivPower?.setOnLongClickListener {
            powerHandler(it as ImageView)
        }

        binding?.ivPower?.setOnClickListener {
            viewModel.powerClick()
        }

        binding?.staffSlider?.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener{
            override fun onSlideComplete(view: SlideToActView) {
                view.resetSlider()
            }
        }
    }

    private fun powerHandler(it: ImageView): Boolean {
        return true
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()
        memberAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        memberAdapter.stopListening()
    }

    override fun click(S: User, idSnapshot: String) {
        TODO("Not yet implemented")
    }

    override fun size(itemCount: Int) {
        binding?.staffSlider?.isLocked = itemCount > 0
    }
}