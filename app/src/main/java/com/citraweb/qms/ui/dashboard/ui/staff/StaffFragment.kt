package com.citraweb.qms.ui.dashboard.ui.staff

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.citraweb.qms.R
import com.citraweb.qms.data.user.User
import com.citraweb.qms.databinding.DialogInfoDepartmentBinding
import com.citraweb.qms.databinding.FragmentStaffBinding
import com.citraweb.qms.databinding.ItemDepartmentBinding
import com.citraweb.qms.ui.MyViewModelFactory
import com.citraweb.qms.utils.Result
import com.citraweb.qms.utils.StateDepartment
import com.citraweb.qms.utils.toas
import com.ncorti.slidetoact.SlideToActView
import kotlinx.coroutines.ExperimentalCoroutinesApi


class StaffFragment : Fragment(), FireMemberAdapter.OnItemClick {

    private lateinit var departmentName: String
    private lateinit var company: String
    private var binding: FragmentStaffBinding? = null
    private lateinit var viewModel: StaffViewModel
    private lateinit var memberAdapter: FireMemberAdapter
    private val iconPower = listOf(
            R.drawable.ic_baseline_stop_24,
            R.drawable.ic_baseline_play_arrow_24
    )
    private lateinit var powerStatus: StateDepartment
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

                        it.companyId?.let { it1 ->
                            company = it1
                        }

                        it.name?.let { it1 ->
                            departmentName = it1
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
            if (departmentName.isEmpty() || company.isEmpty()) {
                dialogInfoDepartment()
                true
            } else {
                viewModel.powerLongClick(powerStatus, departmentName, company)
                true
            }
        }

        binding?.ivPower?.setOnClickListener {
            viewModel.powerClick(powerStatus)
        }

        binding?.staffSlider?.onSlideCompleteListener =
                object : SlideToActView.OnSlideCompleteListener {
                    override fun onSlideComplete(view: SlideToActView) {
                        if (currentIndexWaiting < amountQueue) {
                            val nextIndex = currentIndexWaiting + 1
                            viewModel.setQueue(nextIndex)
                            binding?.staffSlider?.bumpVibration = 50
                        }
                    }
                }
    }

    private fun dialogInfoDepartment() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("With EditText")
        val dialogLayout = DialogInfoDepartmentBinding
                .inflate(LayoutInflater.from(requireActivity()), null, false)
        val edtCompany = dialogLayout.tietDepartmentCompany
        val edtName = dialogLayout.tietDepartmentName
        builder.setView(dialogLayout.root)
        builder.setPositiveButton("OK") { dialogInterface, i ->
            company = edtCompany.text.toString()
            departmentName = edtName.text.toString()
            viewModel.powerLongClick(powerStatus, departmentName, company)
        }
        builder.show()
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
        viewModel.call(S, departmentName, company)
    }

    override fun size(itemCount: Int) {
        binding?.staffSlider?.isLocked = itemCount < 2
    }
}