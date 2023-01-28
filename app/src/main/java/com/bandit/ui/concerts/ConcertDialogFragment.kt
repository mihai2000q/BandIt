package com.bandit.ui.concerts

import android.app.ActionBar
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bandit.constant.BandItEnums
import com.bandit.databinding.DialogFragmentConcertBinding
import com.bandit.extension.StringExtensions.get2Characters
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

open class ConcertDialogFragment: DialogFragment(), AdapterView.OnItemSelectedListener {
    
    private var _binding: DialogFragmentConcertBinding? = null
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var timePickerDialog: TimePickerDialog
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
        datePickerDialog()
        timePickerDialog()
        binding.concertEtDate.setOnClickListener { datePickerDialog.show() }
        binding.concertEtTime.setOnClickListener { timePickerDialog.show() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected fun parseDateTime(): LocalDateTime {
        with(binding) {
            return LocalDateTime.parse(
                "${
                    if (concertEtDate.text.isNullOrEmpty())
                        LocalDate.now().toString()
                    else concertEtDate.text
                }T" +
                        "${
                            if (concertEtTime.text.isNullOrEmpty())
                                LocalTime.MIDNIGHT.minusMinutes(1).toString()
                            else
                                concertEtTime.text
                        }"
            )
        }
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

    private fun datePickerDialog() {
        val calendar = Calendar.getInstance()
        datePickerDialog = DatePickerDialog(
            this.requireContext(),
            { _, year, month, dayOfMonth ->
                binding.concertEtDate.setText(buildString {
                    append("$year-")
                    append("${(month + 1).toString().get2Characters()}-")
                    append(dayOfMonth.toString().get2Characters())
                })
                datePickerDialog.dismiss()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = calendar.timeInMillis
    }

    private fun timePickerDialog() {
        val calendar = Calendar.getInstance()
        timePickerDialog = TimePickerDialog(
            this.requireContext(),
            { _, hourOfDay, minute ->
                binding.concertEtTime.setText(buildString {
                    append("${hourOfDay.toString().get2Characters()}:")
                    append(minute.toString().get2Characters())
                })
                timePickerDialog.dismiss()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        typeIndex = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}