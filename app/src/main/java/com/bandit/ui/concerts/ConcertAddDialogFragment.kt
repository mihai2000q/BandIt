package com.bandit.ui.concerts

import android.annotation.SuppressLint
import android.app.ActionBar.LayoutParams
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bandit.data.model.Concert
import com.bandit.databinding.DialogFragmentConcertAddBinding
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.di.DILocator
import com.bandit.extension.StringExtensions.get2Characters
import com.bandit.util.AndroidUtils
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

class ConcertAddDialogFragment : DialogFragment(), OnItemSelectedListener {

    private var _binding: DialogFragmentConcertAddBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ConcertsViewModel by activityViewModels()
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var timePickerDialog: TimePickerDialog
    private var typeIndex: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentConcertAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.dialog?.window?.setLayout(LayoutParams.WRAP_CONTENT,
            AndroidUtils.getScreenHeight(this.requireActivity())
                    - AndroidUtils.getScreenHeight(this.requireActivity()) / 3
        )
        datePickerDialog()
        timePickerDialog()
        with(binding) {
            concertAddDate.setOnClickListener { datePickerDialog.show() }
            concertAddTime.setOnClickListener { timePickerDialog.show() }
            spinnerType()
            concertBtAdd.setOnClickListener {
                AndroidUtils.hideKeyboard(
                    super.requireActivity(),
                    Context.INPUT_METHOD_SERVICE,
                    concertBtAdd
                )
                viewModel.addConcert(
                    Concert(
                        concertAddName.text.toString(),
                        LocalDateTime.parse(
                            "${if(concertAddDate.text.isNullOrEmpty()) 
                                LocalDate.now().toString() 
                            else concertAddDate.text}T" +
                                "${if(concertAddTime.text.isNullOrEmpty())
                                    LocalTime.MIDNIGHT.minusMinutes(1).toString()
                                else
                                    concertAddTime.text}"),
                        concertAddCity.text.toString(),
                        concertAddCountry.text.toString(),
                        concertAddPlace.text.toString(),
                        BandItEnums.Concert.Type.values()[typeIndex],
                        userUid = DILocator.authenticator.currentUser?.uid
                    )
                )
                super.dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("SetTextI18n")
    private fun datePickerDialog() {
        val calendar = Calendar.getInstance()
        datePickerDialog = DatePickerDialog(
            this.requireContext(),
            { _, year, month, dayOfMonth ->
                binding.concertAddDate.setText("$year-" +
                        "${(month + 1).toString().get2Characters()}-" +
                        dayOfMonth.toString().get2Characters())
                datePickerDialog.dismiss()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = calendar.timeInMillis
    }

    @SuppressLint("SetTextI18n")
    private fun timePickerDialog() {
        val calendar = Calendar.getInstance()
        timePickerDialog = TimePickerDialog(
            this.requireContext(),
            { _, hourOfDay, minute ->
                binding.concertAddTime.setText("${hourOfDay.toString().get2Characters()}:" +
                        minute.toString().get2Characters())
                timePickerDialog.dismiss()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
    }

    private fun spinnerType() {
        with(binding) {
            val adapter = ArrayAdapter(
                super.requireContext(),
                android.R.layout.simple_spinner_item,
                BandItEnums.Concert.Type.values()
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            concertAddSpinnerType.adapter = adapter
            concertAddSpinnerType.onItemSelectedListener = this@ConcertAddDialogFragment
        }
    }

    companion object {
        const val TAG = Constants.Concert.ADD_CONCERT_TAG
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        typeIndex = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}