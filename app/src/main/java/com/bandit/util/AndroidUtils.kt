package com.bandit.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Insets
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.bandit.LoadingActivity
import com.bandit.R
import com.bandit.constant.Constants
import com.bandit.di.DILocator
import com.bandit.ui.friends.FriendsViewModel
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
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
    fun showKeyboard(activity: Activity, inputMethodService: String, view: View) {
        val input = activity.getSystemService(inputMethodService) as InputMethodManager
        input.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
    @Suppress("deprecation")
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
    @Suppress("deprecation")
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
    fun showDialogFragment(dialogFragment: DialogFragment, childFragmentManager: FragmentManager) {
        if(!dialogFragment.isVisible)
            dialogFragment.show(
                childFragmentManager,
                dialogFragment::class.java.fields.filter { it.name == "TAG" }[0].get(null) as String
            )
    }
    fun ifNullHide(
        textView: TextView,
        string: String?
    ) {
        if(string.isNullOrEmpty())
            textView.visibility = View.GONE
        else
            textView.text = string
    }
    fun durationEditTextSetup(editText: EditText) {
        var backspace = false
        editText.setOnKeyListener { _, keyCode, event ->
            if((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)) {
                backspace = true
                return@setOnKeyListener false
            }
            backspace = false
            return@setOnKeyListener false
        }
        editText.addTextChangedListener {
            if(backspace) return@addTextChangedListener
            if(it.toString().length == 2) {
                editText.setText(buildString {
                    append(editText.text)
                    append(":")
                })
                editText.setSelection(3)
            }
        }
    }
    suspend fun loadTask(
        activity: AppCompatActivity,
        task: suspend () -> Boolean?
    ) : Boolean?
    = coroutineScope {
        async {
            var result: Boolean? = null
            LoadingActivity.finish.value = false
            activity.startActivity(Intent(activity, LoadingActivity::class.java))
            launch { result = task() }.join()
            LoadingActivity.finish.value = true
            return@async result
        }
    }.await()

    fun loadTask(
        fragment: Fragment,
        task: suspend () -> Unit
    ) {
        fragment.lifecycleScope.launch {
            LoadingActivity.finish.value = false
            fragment.startActivity(Intent(fragment.context, LoadingActivity::class.java))
            launch { task() }.join()
            LoadingActivity.finish.value = true
        }
    }

    suspend fun loadTaskWithDestination(
        fragment: Fragment,
        task: suspend () -> Boolean?
    ) : Boolean?
       = coroutineScope {
            async {
                var result: Boolean? = null
                LoadingActivity.finish.value = false
                fragment.startActivity(Intent(fragment.context, LoadingActivity::class.java))
                launch { result = task() }.join()
                LoadingActivity.finish.value = true
                return@async result
            }
        }.await()

    suspend fun isNetworkAvailable() = DILocator.database.isConnected()
    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        //TODO: Replace deprecated method for .insertImage()
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    fun setProfilePicture(
        fragment: Fragment,
        viewModel: FriendsViewModel,
        profilePicView: ImageView,
        userUid: String?
    ) {
        fragment.lifecycleScope.launch {
            val pic = viewModel.getProfilePicture(userUid)
            Glide.with(fragment)
                .load(if(pic.isEmpty()) R.drawable.default_avatar else pic)
                .placeholder(R.drawable.placeholder_profile_pic)
                .into(profilePicView)
        }
    }
}
