package com.citraweb.qms.ui.dashboard.ui.staff

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.citraweb.qms.R
import com.citraweb.qms.data.user.User
import com.citraweb.qms.databinding.FragmentStaffBinding
import com.citraweb.qms.ui.MyViewModelFactory
import com.citraweb.qms.utils.Result
import com.citraweb.qms.utils.StateDepartment
import com.citraweb.qms.utils.toas
import com.ncorti.slidetoact.SlideToActView


class StaffFragment : Fragment(), FireMemberAdapter.OnItemClick {

    private var binding: FragmentStaffBinding? = null
    private lateinit var viewModel: StaffViewModel
    private lateinit var memberAdapter: FireMemberAdapter
    private val iconPower = listOf(
            R.drawable.ic_baseline_stop_24,
            R.drawable.ic_baseline_play_arrow_24
    )
    private lateinit var powerStatus : StateDepartment
    private var currentIndexWaiting = 0
    private var amountQueue = 0


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
        viewModel.department.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Result.Success -> {
                    val department = result.data
                    department?.let {
                        it.status?.let { it1 ->
                            powerStatus = StateDepartment.valueOf(it1)
                            binding?.ivPower?.setImageResource(iconPower[powerStatus.ordinal])
                        }

                        it.currentQueue?.let { it1 ->
                            if (currentIndexWaiting < it1) {
                                binding?.staffSlider?.resetSlider()
                                currentIndexWaiting = it1
                            }
                        }

                        it.waitings?.let { it1 ->
                            amountQueue = it1.size
                        }
                    }
                }
                is Result.Error -> {
                }
                is Result.Canceled -> {
                }
            }
        })

        viewModel.toast.observe(viewLifecycleOwner, Observer {
            val message = it ?: return@Observer
            view.context.toas(message).show()
        })

        memberAdapter = FireMemberAdapter(viewModel.query, this)

        binding?.rvStaff?.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = this@StaffFragment.memberAdapter
        }

        memberAdapter.notifyDataSetChanged()

        binding?.ivPower?.setOnLongClickListener {
            viewModel.powerLongClick(powerStatus)
            true
        }

        binding?.ivPower?.setOnClickListener {
            viewModel.powerClick(powerStatus)
        }

        binding?.staffSlider?.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener {
            override fun onSlideComplete(view: SlideToActView) {
                if (currentIndexWaiting < amountQueue) {
                    val nextIndex = currentIndexWaiting + 1
                    viewModel.setQueue(nextIndex)
                    binding?.staffSlider?.bumpVibration = 50
                }
            }
        }
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
        binding?.staffSlider?.isLocked = itemCount < 2
    }
}