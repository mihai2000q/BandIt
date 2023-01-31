package com.bandit.builder

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.bandit.R
import com.bandit.data.model.Band
import com.bandit.extension.get2Characters
import com.bandit.ui.account.AccountDialogFragment
import com.bandit.ui.band.BandDialogFragment
import com.bandit.ui.band.CreateBandDialogFragment
import com.bandit.util.AndroidUtils
import java.util.*

object AndroidComponents {
    fun datePickerDialog(
        context: Context,
        editText: EditText
    ) : DatePickerDialog {
        val calendar = Calendar.getInstance()
        lateinit var datePickerDialog: DatePickerDialog
        datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                editText.setText(buildString {
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
        return datePickerDialog
    }

    fun timePickerDialog(
        context: Context,
        editText: EditText
    ) : TimePickerDialog {
        val calendar = Calendar.getInstance()
        lateinit var timePickerDialog: TimePickerDialog
        timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                editText.setText(buildString {
                    append("${hourOfDay.toString().get2Characters()}:")
                    append(minute.toString().get2Characters())
                })
                timePickerDialog.dismiss()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        return timePickerDialog
    }

    fun header(
        activity: FragmentActivity,
        accountButton: ImageButton,
        bandButton: Button,
        viewLifecycleOwner: LifecycleOwner,
        band: LiveData<Band>
    ) {
        val accountDialogFragment = AccountDialogFragment(accountButton)
        val createBandDialogFragment = CreateBandDialogFragment()
        val bandDialogFragment = BandDialogFragment()
        accountButton(
            activity,
            accountButton,
            accountDialogFragment
        )
        bandButton(
            activity,
            bandButton,
            band,
            viewLifecycleOwner,
            createBandDialogFragment,
            bandDialogFragment
        )
    }

    private fun accountButton(
        activity: FragmentActivity,
        button: ImageButton,
        accountDialogFragment: AccountDialogFragment
    ) {
        button.setOnClickListener {
            AndroidUtils.showDialogFragment(
                accountDialogFragment,
                activity.supportFragmentManager
            )
            button.setImageDrawable(
                ContextCompat.getDrawable(
                    activity.applicationContext,
                    R.drawable.ic_baseline_account_clicked
                )
            )
        }
    }
    private fun bandButton(
        activity: FragmentActivity,
        button: Button,
        band: LiveData<Band>,
        viewLifecycleOwner: LifecycleOwner,
        createBandDialogFragment: CreateBandDialogFragment,
        bandDialogFragment: BandDialogFragment
    ) {
        band.observe(viewLifecycleOwner) {
            if (it.isEmpty())
                button.setOnClickListener {
                    AndroidUtils.showDialogFragment(
                        createBandDialogFragment,
                        activity.supportFragmentManager
                    )
                }
            else {
                button.text = it.name
                button.setOnClickListener {
                    AndroidUtils.showDialogFragment(
                        bandDialogFragment,
                        activity.supportFragmentManager
                    )
                }
            }
        }
    }
}