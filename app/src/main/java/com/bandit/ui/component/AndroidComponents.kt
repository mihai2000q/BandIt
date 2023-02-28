package com.bandit.ui.component

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.View
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.bandit.extension.get2Characters
import com.google.android.material.snackbar.Snackbar
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

    fun alertDialog(
        context: Context,
        title: String,
        message: String,
        positive: String,
        negative: String,
        onPositiveAction: () -> Unit
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(positive) { _, _ -> onPositiveAction() }
        builder.setNegativeButton(negative) { _, _ -> }
        builder.create().show()
    }

    fun snackbarNotification(
        view: View,
        message: String,
        text: String,
        action: (() -> Unit)? = null
    ) {
        val snack = Snackbar
            .make(
                view,
                message,
                Snackbar.LENGTH_INDEFINITE
            )
            .setAction(text) {
                action?.invoke()
            }
        snack.show()
    }

    fun toastNotification(context: Context, message: String,
                          length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, length).show()
    }

}