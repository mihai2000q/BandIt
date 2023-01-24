package com.bandit.util

import android.app.Activity
import android.content.Context
import android.graphics.Insets
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.bandit.R
import com.bandit.constant.Constants
import com.bandit.di.DILocator
import com.bandit.ui.band.BandDialogFragment
import com.bandit.ui.band.CreateBandDialogFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlin.random.Random


object AndroidUtils {
    fun generateRandomLong() = Random.nextLong(Constants.MAX_NR_ITEMS)
    fun unlockNavigation(bottomNavigationView: BottomNavigationView?, drawerLayout: DrawerLayout?) {
        bottomNavigationView?.visibility = View.VISIBLE
        drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }
    fun lockNavigation(bottomNavigationView: BottomNavigationView?, drawerLayout: DrawerLayout?) {
        drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        bottomNavigationView?.visibility = View.INVISIBLE
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
    fun bandButton(
        activity: FragmentActivity,
        button: Button,
        createBandDialogFragment: CreateBandDialogFragment,
        bandDialogFragment: BandDialogFragment
    ) {
        if(DILocator.database.currentBand.isEmpty())
            button.setOnClickListener {
                showDialogFragment(createBandDialogFragment, activity.supportFragmentManager)
            }
        else {
            button.text = DILocator.database.currentBand.name
            button.setOnClickListener {
                showDialogFragment(bandDialogFragment, activity.supportFragmentManager)
            }
        }
    }
}
