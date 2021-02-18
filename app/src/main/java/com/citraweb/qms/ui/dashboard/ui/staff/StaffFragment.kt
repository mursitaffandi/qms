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
import com.citraweb.qms.databinding.DialogInfoDepartmentBinding
import com.citraweb.qms.databinding.FragmentStaffBinding
import com.citraweb.qms.ui.MyViewModelFactory
import com.citraweb.qms.ui.dashboard.ui.queue.FireQueueAdapter
import com.citraweb.qms.utils.Result
import com.citraweb.qms.utils.StateDepartment
import com.citraweb.qms.utils.toas
import com.ncorti.slidetoact.SlideToActView
import timber.log.Timber


class StaffFragment : Fragment(), FireQueueAdapter.OnItemClick {

    private lateinit var powerStatus: StateDepartment
    private lateinit var departmentName: String
    private lateinit var company: String
    private lateinit var prefix: String
    private var queue = listOf<String>()
    private var currentIndexWaiting = 0
    private var binding: FragmentStaffBinding? = null
    private lateinit var viewModel: StaffViewModel
    private lateinit var adapter: FireQueueAdapter
    private val iconPower = listOf(
            R.drawable.ic_baseline_stop_24,
            R.drawable.ic_baseline_play_arrow_24
    )


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentStaffBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        viewModel = ViewModelProvider(this, MyViewModelFactory()).get(StaffViewModel::class.java)
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
                            queue = it1
                        }

                        it.companyId?.let { it1 ->
                            company = it1
                        }

                        it.name?.let { it1 ->
                            departmentName = it1
                        }

                        it.prefix?.let { it1 ->
                            prefix = it1
                        }
                    }
                }
                is Result.Error -> {
                    Timber.e(result.exception)
                }
                is Result.Canceled -> {
                    Timber.e(result.exception)
                }
            }
        })

        viewModel.toast.observe(viewLifecycleOwner, Observer {
            val message = it ?: return@Observer
            view.context.toas(message).show()
        })

        adapter = FireQueueAdapter(viewModel.query, this)

        binding?.rvStaff?.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = this@StaffFragment.adapter
        }
        adapter.notifyDataSetChanged()

        binding?.ivPower?.setOnLongClickListener {
            if (departmentName.isEmpty() || company.isEmpty() || prefix.isEmpty())
                dialogInfoDepartment()
            else
                viewModel.powerLongClick(powerStatus, departmentName, company, prefix)
            true
        }

        binding?.ivPower?.setOnClickListener {
            viewModel.powerClick(powerStatus)
        }

        binding?.staffSlider?.onSlideCompleteListener =
                object : SlideToActView.OnSlideCompleteListener {
                    override fun onSlideComplete(view: SlideToActView) {
                        if (currentIndexWaiting < queue.size) {
                            val nextIndex = currentIndexWaiting + 1
                            viewModel.setQueue(nextIndex, queue[nextIndex], departmentName, company)
                            binding?.staffSlider?.bumpVibration = 50
                        }
                    }
                }
    }

    private fun dialogInfoDepartment() {
        val builder = AlertDialog.Builder(requireActivity())
        val dialogLayout = DialogInfoDepartmentBinding.inflate(LayoutInflater.from(context), null, false)
        builder.setTitle(context?.getString(R.string.title_dialog_department))
        builder.setView(dialogLayout.root)
        builder.setPositiveButton("OK") { dialogInterface, i ->
            company = dialogLayout.tietDepartmentCompany.text.toString()
            departmentName = dialogLayout.tietDepartmentName.text.toString()
            prefix = dialogLayout.tietDepartmentPrefix.text.toString()
            if (departmentName.isEmpty() || company.isEmpty() || prefix.isEmpty()) {
                return@setPositiveButton
            } else {
                viewModel.powerLongClick(powerStatus, departmentName, company, prefix)
            }
        }
        builder.show()
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

    override fun click(idUser: String?, departmentId: String?) {
        idUser?.let {
            viewModel.call(it, departmentName, company)
        }
    }

    override fun size(itemCount: Int) {
        binding?.staffSlider?.isLocked = itemCount < 2
    }
}