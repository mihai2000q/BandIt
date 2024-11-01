package com.bandit.ui.template

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TableRow
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bandit.constant.BandItEnums
import com.bandit.databinding.DialogFragmentConcertBinding
import com.bandit.di.DILocator
import com.bandit.ui.component.AndroidComponents
import com.bandit.ui.concerts.ConcertsViewModel
import com.bandit.util.AndroidUtils

abstract class ConcertDialogFragment: DialogFragment(), AdapterView.OnItemSelectedListener {
    
    private var _binding: DialogFragmentConcertBinding? = null
    protected val binding get() = _binding!!
    protected val viewModel: ConcertsViewModel by activityViewModels()
    protected var typeIndex: Int = 0
    private val validatorService by lazy { DILocator.getValidatorService(super.requireActivity()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentConcertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.dialog?.window?.setLayout(
            AndroidUtils.getScreenWidth(super.requireActivity()),
            TableRow.LayoutParams.WRAP_CONTENT
        )
        with(binding) {
            AndroidComponents.datePickerDialog(super.requireContext(), concertEtDate) {
                AndroidUtils.hideKeyboard(
                    super.requireActivity(),
                    Context.INPUT_METHOD_SERVICE,
                    concertEtDate
                )
            }
            AndroidComponents.timePickerDialog(super.requireContext(), concertEtTime) {
                AndroidUtils.hideKeyboard(
                    super.requireActivity(),
                    Context.INPUT_METHOD_SERVICE,
                    concertEtTime
                )
            }
            AndroidUtils.durationEditTextSetup(concertEtDuration)
            concertEtDate.minWidth = AndroidUtils.getScreenWidth(super.requireActivity()) * 7 / 16
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected fun spinnerType() {
        AndroidComponents.spinner(
            super.requireContext(),
            binding.concertEtSpinnerType,
            this@ConcertDialogFragment,
            BandItEnums.Concert.Type.values()
        )
    }

    protected open fun validateFields(): Boolean {
        return  validatorService.validateName(binding.concertEtName, binding.concertEtNameLayout) &&
                validatorService.validateDuration(binding.concertEtDuration, binding.concertEtDurationLayout)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        typeIndex = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}