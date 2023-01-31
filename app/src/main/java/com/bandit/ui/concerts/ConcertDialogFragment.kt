package com.bandit.ui.concerts

import android.app.ActionBar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bandit.builder.AndroidComponents
import com.bandit.constant.BandItEnums
import com.bandit.databinding.DialogFragmentConcertBinding
import com.bandit.util.AndroidUtils

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
        this.dialog?.window?.setLayout(
            ActionBar.LayoutParams.WRAP_CONTENT,
            ActionBar.LayoutParams.WRAP_CONTENT
        )
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
        with(binding) {
            val adapter = ArrayAdapter(
                super.requireContext(),
                android.R.layout.simple_spinner_item,
                BandItEnums.Concert.Type.values()
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            concertEtSpinnerType.adapter = adapter
            concertEtSpinnerType.onItemSelectedListener = this@ConcertDialogFragment
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        typeIndex = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}