package com.bandit.ui.concerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bandit.builder.AndroidComponents
import com.bandit.constant.BandItEnums
import com.bandit.databinding.DialogFragmentConcertBinding

open class ConcertDialogFragment: DialogFragment(), AdapterView.OnItemSelectedListener {
    
    private var _binding: DialogFragmentConcertBinding? = null
    protected val binding get() = _binding!!
    protected val viewModel: ConcertsViewModel by activityViewModels()
    protected var typeIndex: Int = 0

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
        with(binding) {
            val datePickerDialog = AndroidComponents.datePickerDialog(super.requireContext(), concertEtDate)
            val timePickerDialog = AndroidComponents.timePickerDialog(super.requireContext(), concertEtTime)
            concertEtDate.setOnClickListener { datePickerDialog.show() }
            concertEtTime.setOnClickListener { timePickerDialog.show() }
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

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        typeIndex = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}