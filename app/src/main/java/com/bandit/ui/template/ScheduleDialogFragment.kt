package com.bandit.ui.template

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.TableRow
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bandit.R
import com.bandit.ui.component.AndroidComponents
import com.bandit.constant.BandItEnums
import com.bandit.databinding.DialogFragmentScheduleBinding
import com.bandit.di.DILocator
import com.bandit.service.IValidatorService
import com.bandit.ui.schedule.ScheduleViewModel
import com.bandit.util.AndroidUtils

abstract class ScheduleDialogFragment : DialogFragment(), OnItemSelectedListener {

    private var _binding: DialogFragmentScheduleBinding? = null
    private lateinit var validatorService: IValidatorService
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
        validatorService = DILocator.getValidatorService(super.requireActivity())
        this.dialog?.window?.setLayout(
            AndroidUtils.getScreenWidth(super.requireActivity()),
            TableRow.LayoutParams.WRAP_CONTENT
        )
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
        return  validatorService.validateName(binding.scheduleEtName) &&
                validatorService.validateDate(binding.scheduleEtDate) &&
                validatorService.validateTime(binding.scheduleEtTime) &&
                validatorService.validateDuration(binding.scheduleEtDuration)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        typeIndex = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}