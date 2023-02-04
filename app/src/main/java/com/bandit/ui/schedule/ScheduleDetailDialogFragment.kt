package com.bandit.ui.schedule

import android.app.ActionBar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bandit.constant.Constants
import com.bandit.data.model.Event
import com.bandit.databinding.DialogFragmentScheduleDetailBinding
import com.bandit.extension.normalizeWord
import com.bandit.extension.print
import com.bandit.extension.printName
import com.bandit.util.AndroidUtils

class ScheduleDetailDialogFragment : DialogFragment() {

    private var _binding: DialogFragmentScheduleDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ScheduleViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentScheduleDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.selectedEvent.observe(viewLifecycleOwner) { assignEventDetails(it) }
    }

    private fun assignEventDetails(event: Event) {
        with(binding) {
            scheduleDetailName.text = event.name
            scheduleDetailDateTime.text = event.dateTime.printName()
            scheduleDetailType.text = event.type.name.normalizeWord()
            scheduleDetailDuration.text = event.duration.print()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = Constants.Schedule.DETAIL_TAG
    }
}