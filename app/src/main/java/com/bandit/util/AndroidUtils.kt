package com.bandit.util

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.bandit.constant.Constants
import kotlin.random.Random

object AndroidUtils {
    fun generateRandomId() = Random.nextInt(Constants.MAX_NR_ITEMS)
    //Context.INPUT_METHOD_SERVICE
    fun hideKeyboard(activity: Activity, inputMethodService: String, view: View) {
        val input = activity.getSystemService(inputMethodService) as InputMethodManager
        input.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
