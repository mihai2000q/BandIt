package com.bandit.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import com.bandit.R
import com.bandit.builder.AndroidComponents
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.data.model.Event
import com.bandit.databinding.DialogFragmentScheduleAddBinding
import com.bandit.di.DILocator
import com.bandit.util.AndroidUtils
import com.bandit.util.ParserUtils

class ScheduleAddDialogFragment : DialogFragment(), OnItemSelectedListener {

    private var _binding: DialogFragmentScheduleAddBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ScheduleViewModel by activityViewModels()
    private var typeIndex = 0
    var date: MutableLiveData<String?> = MutableLiveData(null)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentScheduleAddBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            AndroidComponents.spinner(
                super.requireContext(),
                scheduleAddSpinnerType,
                this@ScheduleAddDialogFragment,
                BandItEnums.Event.Type.values()
            )
            val datePickerDialog = AndroidComponents.datePickerDialog(
                super.requireContext(),
                scheduleAddEtDate
            )
            val timePickerDialog = AndroidComponents.timePickerDialog(
                super.requireContext(),
                scheduleAddEtTime
            )
            scheduleAddEtDate.setOnClickListener { datePickerDialog.show() }
            scheduleAddEtTime.setOnClickListener { timePickerDialog.show() }
            AndroidUtils.durationEditTextSetup(scheduleAddEtDuration)
            date.observe(viewLifecycleOwner) { scheduleAddEtDate.setText(date.value) }
            scheduleAddButton.setOnClickListener {
                viewModel.addEvent(
                    Event(
                        name = scheduleAddEtName.text.toString(),
                        dateTime = ParserUtils.parseDateTime(scheduleAddEtDate.text.toString(),
                            scheduleAddEtTime.text.toString()),
                        duration = ParserUtils.parseDuration(scheduleAddEtDuration.text.toString()),
                        type = BandItEnums.Event.Type.values()[typeIndex],
                        bandId = DILocator.database.currentBand.id
                    )
                )
                AndroidUtils.toastNotification(
                    super.requireContext(),
                    resources.getString(R.string.event_add_toast)
                )
                scheduleAddEtName.setText("")
                scheduleAddEtDate.setText("")
                scheduleAddEtTime.setText("")
                scheduleAddEtDuration.setText("")
                super.dismiss()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        typeIndex = 0
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    companion object {
        const val TAG = Constants.Schedule.ADD_TAG
    }
}