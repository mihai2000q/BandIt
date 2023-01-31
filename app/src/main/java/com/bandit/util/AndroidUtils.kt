package com.bandit.util

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Insets
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.bandit.R
import com.bandit.constant.Constants
import com.bandit.data.model.Band
import com.bandit.extension.StringExtensions.get2Characters
import com.bandit.ui.account.AccountDialogFragment
import com.bandit.ui.band.BandDialogFragment
import com.bandit.ui.band.CreateBandDialogFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlin.random.Random


object AndroidUtils {
    fun generateRandomLong() = Random.nextLong(Constants.MAX_NR_ITEMS)
    fun unlockNavigation(bottomNavigationView: BottomNavigationView?, drawerLayout: DrawerLayout?) {
        bottomNavigationView?.visibility = View.VISIBLE
        drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }
    fun lockNavigation(bottomNavigationView: BottomNavigationView?, drawerLayout: DrawerLayout?) {
        drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        bottomNavigationView?.visibility = View.GONE
    }
    fun hideKeyboard(activity: Activity, inputMethodService: String, view: View) {
        val input = activity.getSystemService(inputMethodService) as InputMethodManager
        input.hideSoftInputFromWindow(view.windowToken, 0)
    }
    fun getScreenWidth(activity: Activity): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }
    fun getScreenHeight(activity: Activity): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.height() - insets.top - insets.bottom
        } else {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.heightPixels
        }
    }
    fun toastNotification(context: Context, message: String,
                          length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, length).show()
    }
    fun showDialogFragment(dialogFragment: DialogFragment, childFragmentManager: FragmentManager) {
        if(!dialogFragment.isVisible)
            dialogFragment.show(
                childFragmentManager,
                dialogFragment::class.java.fields.filter { it.name == "TAG" }[0].get(null) as String
            )
    }
    fun accountButton(
        activity: FragmentActivity,
        button: ImageButton,
        accountDialogFragment: AccountDialogFragment
    ) {
        button.setOnClickListener {
            this.showDialogFragment(
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
    fun bandButton(
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
                    showDialogFragment(
                        createBandDialogFragment,
                        activity.supportFragmentManager
                    )
                }
            else {
                button.text = it.name
                button.setOnClickListener {
                    showDialogFragment(
                        bandDialogFragment,
                        activity.supportFragmentManager
                    )
                }
            }
        }
    }

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
}
