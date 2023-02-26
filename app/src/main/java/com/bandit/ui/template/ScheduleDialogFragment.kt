package com.bandit.ui.template

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bandit.R
import com.bandit.ui.component.AndroidComponents
import com.bandit.constant.BandItEnums
import com.bandit.databinding.DialogFragmentScheduleBinding
import com.bandit.ui.schedule.ScheduleViewModel
import com.bandit.util.AndroidUtils

abstract class ScheduleDialogFragment : DialogFragment(), OnItemSelectedListener {

    private var _binding: DialogFragmentScheduleBinding? = null
    protected val binding get() = _binding!!
    protected val viewModel: ScheduleViewModel by activityViewModels()
    protected var typeIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentScheduleBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            AndroidComponents.spinner(
                super.requireContext(),
                scheduleSpinnerType,
                this@ScheduleDialogFragment,
                BandItEnums.Event.Type.values()
            )
            AndroidComponents.datePickerDialog(super.requireContext(), scheduleEtDate)
            AndroidComponents.timePickerDialog(super.requireContext(), scheduleEtTime)
            AndroidUtils.durationEditTextSetup(scheduleEtDuration)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    protected open fun validateFields(): Boolean {
        with(binding) {
            if(scheduleEtName.text.isNullOrEmpty()) {
                scheduleEtName.error = resources.getString(R.string.et_name_validation)
                return false
            }
            if(scheduleEtDate.text.isNullOrEmpty()) {
                scheduleEtDate.error = resources.getString(R.string.et_date_validation)
                return false
            }
            if(scheduleEtTime.text.isNullOrEmpty()) {
                scheduleEtTime.error = resources.getString(R.string.et_time_validation)
                return false
            }
        }
        return true
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        typeIndex = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}