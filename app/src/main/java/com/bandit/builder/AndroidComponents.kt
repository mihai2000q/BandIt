package com.bandit.builder

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
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
        editText: EditText,
        withPast: Boolean = false,
        hideKeyboard: (() -> Unit)? = null
    ) {
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
        datePickerDialog.datePicker.minDate = if(withPast) 0 else calendar.timeInMillis
        editText.showSoftInputOnFocus = false
        editText.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus) {
                datePickerDialog.show()
                hideKeyboard?.invoke()
            }
        }
        editText.setOnClickListener {
            datePickerDialog.show()
            hideKeyboard?.invoke()
        }
    }

    fun timePickerDialog(
        context: Context,
        editText: EditText,
        hideKeyboard: (() -> Unit)? = null
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
        editText.showSoftInputOnFocus = false
        editText.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus) {
                timePickerDialog.show()
                hideKeyboard?.invoke()
            }
        }
        editText.setOnClickListener {
            timePickerDialog.show()
            hideKeyboard?.invoke()
        }
        return timePickerDialog
    }

    fun <T> spinner(
        context: Context,
        spinner: Spinner,
        onItemSelectedListener: OnItemSelectedListener,
        objects: Array<T>
    ) {
        val adapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_item,
            objects
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = onItemSelectedListener
    }

    fun alertDialogDelete(
        context: Context,
        title: String,
        message: String,
        positive: String,
        negative: String,
        deleteAction: () -> Unit
    ): AlertDialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(positive) { _, _ -> deleteAction() }
        builder.setNegativeButton(negative) { _, _ -> }
        return builder.create()
    }

    fun header(
        activity: FragmentActivity,
        accountButton: ImageButton,
    ) {
        val accountDialogFragment = AccountDialogFragment(accountButton)
        accountButton(
            activity,
            accountButton,
            accountDialogFragment
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