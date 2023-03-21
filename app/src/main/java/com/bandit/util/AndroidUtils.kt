package com.bandit.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Insets
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowInsets
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bandit.MainActivity
import com.bandit.R
import com.bandit.constant.Constants
import com.bandit.data.model.Band
import com.bandit.di.DILocator
import com.bandit.ui.component.AndroidComponents
import com.bandit.ui.component.LoadingActivity
import com.bandit.ui.component.LoadingDialogFragment
import com.bandit.ui.friends.FriendsViewModel
import com.bumptech.glide.Glide
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random


object AndroidUtils {
    fun fromCalendarToLocalDate(calendar: Calendar): LocalDate =
        LocalDateTime.ofInstant(
            calendar.toInstant(),
            calendar.timeZone.toZoneId()
        ).toLocalDate()

    fun generateRandomLong() = Random.nextLong(Constants.MAX_NR_ITEMS)

    fun unlockNavigation(
        activity: Activity
    ) {
        activity.findViewById<BottomNavigationView>(R.id.main_bottom_navigation_view)
            ?.visibility = View.VISIBLE
        activity.findViewById<DrawerLayout>(R.id.main_drawer_layout)
            ?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        activity.invalidateOptionsMenu().apply {
            MainActivity.isAccountButtonShown = true
        }
    }

    fun lockNavigation(
        activity: Activity
    ) {
        activity.findViewById<BottomNavigationView>(R.id.main_bottom_navigation_view)
            ?.visibility = View.INVISIBLE
        activity.findViewById<DrawerLayout>(R.id.main_drawer_layout)
            ?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        activity.invalidateOptionsMenu().apply {
            MainActivity.isAccountButtonShown = false
        }
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
        if(string.isNullOrBlank())
            textView.visibility = View.GONE
        else
            textView.text = string
    }

    fun ifNullHide(
        textView: TextView,
        additional: TextView,
        string: String?
    ) {
        if(string.isNullOrBlank()) {
            textView.visibility = View.GONE
            additional.visibility = View.GONE
        }
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

    fun loadIntent(
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

    suspend fun loadIntent(
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

    suspend fun loadIntentWithDestination(
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

    fun loadDialogFragment(
        viewModelScope: CoroutineScope,
        fragment: Fragment,
        task: suspend () -> Unit
    ) {
        viewModelScope.launch {
            LoadingDialogFragment.finish.value = false
            val loadingDialogFragment = LoadingDialogFragment()
            showDialogFragment(loadingDialogFragment, fragment.childFragmentManager)
            launch { task() }.join()
            LoadingDialogFragment.finish.value = true
        }
    }

    suspend fun isNetworkAvailable() = DILocator.getDatabase().isConnected()

    @Suppress("deprecation")
    fun getImageUri(context: Context, image: Bitmap?): Uri? {
        image?.compress(Bitmap.CompressFormat.JPEG, 100, ByteArrayOutputStream())
        val path = MediaStore.Images.Media
                .insertImage(context.contentResolver, image, "Title", null)
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
                .load(if(pic == Uri.EMPTY) R.drawable.default_avatar else pic)
                .placeholder(R.drawable.placeholder_profile_pic)
                .into(profilePicView)
        }
    }

    fun disableIfBandEmpty(
        viewLifecycleOwner: LifecycleOwner,
        resources: Resources,
        bandLiveData: LiveData<Band>,
        view: View,
        normalAction: () -> Unit
    ) {
        bandLiveData.observe(viewLifecycleOwner) { band ->
            view.setOnClickListener {
                if(band.isEmpty())
                    AndroidComponents.snackbarNotification(
                        view,
                        resources.getString(R.string.empty_band_snackbar),
                        resources.getString(R.string.bt_okay)
                    ).show()
                else
                    normalAction()
            }
        }
    }

    fun <T : Comparable<T>> setRecyclerViewEmpty(
        viewLifecycleOwner: LifecycleOwner,
        list: LiveData<List<T>>,
        rvList: RecyclerView,
        rvEmpty: LinearLayout,
        rvBandEmpty: LinearLayout,
        band: LiveData<Band>,
        adapter: (list: List<T>) -> Adapter<*>,
        additional: (() -> Unit)? = null
    ) {
        list.observe(viewLifecycleOwner) {
            additional?.invoke()
            band.observe(viewLifecycleOwner) { band ->
                if(band.isEmpty()) {
                    rvList.visibility = View.GONE
                    rvEmpty.visibility = View.GONE
                    rvBandEmpty.visibility = View.VISIBLE
                }
                else if(it.isEmpty()) {
                    rvList.visibility = View.GONE
                    rvBandEmpty.visibility = View.GONE
                    rvEmpty.visibility = View.VISIBLE
                } else {
                    rvList.adapter = adapter(it)
                    rvList.visibility = View.VISIBLE
                    rvEmpty.visibility = View.GONE
                    rvBandEmpty.visibility = View.GONE
                }
            }
        }
    }

    fun <T : Comparable<T>> setRecyclerViewEmpty(
        viewLifecycleOwner: LifecycleOwner,
        list: LiveData<List<T>>,
        rvList: RecyclerView,
        rvEmpty: LinearLayout,
        adapter: (list: List<T>) -> Adapter<*>
    ) {
        list.observe(viewLifecycleOwner) {
            if(it.isEmpty()) {
                rvList.visibility = View.GONE
                rvEmpty.visibility = View.VISIBLE
            } else {
                rvList.adapter = adapter(it)
                rvList.visibility = View.VISIBLE
                rvEmpty.visibility = View.GONE
            }
        }
    }

    fun setBadgeDrawableOnView(
        badgeDrawable: BadgeDrawable,
        view: View,
        size: Int,
        isVisible: Boolean,
        backgroundColor: Int
    )  {
        view.viewTreeObserver
            .addOnGlobalLayoutListener(@ExperimentalBadgeUtils object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    badgeDrawable.number = size
                    badgeDrawable.maxCharacterCount = 99
                    badgeDrawable.horizontalOffset = 35
                    badgeDrawable.verticalOffset = 24
                    badgeDrawable.isVisible = isVisible
                    badgeDrawable.backgroundColor = backgroundColor
                    badgeDrawable.badgeGravity = BadgeDrawable.TOP_END
                    BadgeUtils.attachBadgeDrawable(badgeDrawable, view)
                    view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
    }

    fun refreshFragment(navController: NavController) {
        val fragmentId = navController.currentDestination?.id
        navController.popBackStack(fragmentId!!,true)
        navController.navigate(fragmentId)
    }

    fun setupRefreshLayout(
        fragment: Fragment,
        rvList: RecyclerView
    ) {
        fragment.requireActivity().findViewById<Toolbar>(R.id.main_toolbar)
            .setOnClickListener {
                rvList.smoothScrollToPosition(0)
            }
        rvList.setOnScrollChangeListener { _, scrollX, scrollY, _, _ ->
            fragment.requireActivity().findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
                .isEnabled = scrollX == 0 && scrollY == 0
        }
    }

    fun setupFabOptions(
        fragment: Fragment,
        rvList: RecyclerView,
        fabOption: FloatingActionButton,
        buttons: List<FloatingActionButton>,
        textViews: List<TextView>
    ) {
        val zoomOutAnim = AnimationUtils.loadAnimation(fragment.context, R.anim.zoom_out)
        val zoomInAnim = AnimationUtils.loadAnimation(fragment.context, R.anim.zoom_in_delay)
        var optionButtonOpen = false
        fabOption.setOnClickListener {
            optionButtonOpen = if(optionButtonOpen) {
                this.closeAllFAB(fragment.requireContext(), fabOption, buttons, textViews)
                false
            } else {
                this.openAllFAB(fragment.requireContext(), fabOption, buttons, textViews)
                true
            }
        }
        rvList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if(newState != RecyclerView.SCROLL_STATE_SETTLING) {
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        if (optionButtonOpen) {
                            optionButtonOpen = false
                            this@AndroidUtils.closeAllFAB(
                                fragment.requireContext(),
                                fabOption,
                                buttons,
                                textViews
                            )
                        }
                        if(fabOption.visibility == View.VISIBLE) {
                            fabOption.startAnimation(zoomOutAnim)
                            fabOption.postOnAnimation {
                                fabOption.visibility = View.INVISIBLE
                            }
                        }
                    } else {
                        if(fabOption.visibility == View.INVISIBLE) {
                            fabOption.startAnimation(zoomInAnim)
                            fabOption.postOnAnimation {
                                fabOption.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        })
    }

    fun setupFabOptionsWithBand(
        fragment: Fragment,
        rvList: RecyclerView,
        band: LiveData<Band>,
        fabOption: FloatingActionButton,
        buttons: List<FloatingActionButton>,
        textViews: List<TextView>
    ) {
        val zoomOutAnim = AnimationUtils.loadAnimation(fragment.context, R.anim.zoom_out)
        val zoomInAnim = AnimationUtils.loadAnimation(fragment.context, R.anim.zoom_in_delay)
        var optionButtonOpen = false
        fabOption.setOnClickListener {
            if(band.value!!.isEmpty())
                AndroidComponents.snackbarNotification(
                    fabOption,
                    fragment.resources.getString(R.string.empty_band_snackbar),
                    fragment.resources.getString(R.string.bt_okay)
                ).show()
            else {
                optionButtonOpen = if(optionButtonOpen) {
                    this.closeAllFAB(fragment.requireContext(), fabOption, buttons, textViews)
                    false
                } else {
                    this.openAllFAB(fragment.requireContext(), fabOption, buttons, textViews)
                    true
                }
            }
        }
        rvList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if(newState != RecyclerView.SCROLL_STATE_SETTLING) {
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        if (optionButtonOpen) {
                            optionButtonOpen = false
                            this@AndroidUtils.closeAllFAB(
                                fragment.requireContext(),
                                fabOption,
                                buttons,
                                textViews
                            )
                        }
                        if(fabOption.visibility == View.VISIBLE) {
                            fabOption.startAnimation(zoomOutAnim)
                            fabOption.postOnAnimation {
                                fabOption.visibility = View.INVISIBLE
                            }
                        }
                    } else {
                        if(fabOption.visibility == View.INVISIBLE) {
                            fabOption.startAnimation(zoomInAnim)
                            fabOption.postOnAnimation {
                                fabOption.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        })
    }

    fun setupFabScrollUp(
        context: Context,
        rvList: RecyclerView,
        fabScrollUp: FloatingActionButton
    ) {
        val outAnimDelay = AnimationUtils.loadAnimation(context, R.anim.zoom_out_delay)
        val inAnim = AnimationUtils.loadAnimation(context, R.anim.zoom_in)
        fabScrollUp.setOnClickListener {
            rvList.smoothScrollToPosition(0)
        }
        rvList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if(newState != RecyclerView.SCROLL_STATE_SETTLING) {
                    if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if(fabScrollUp.visibility == View.VISIBLE) {
                            fabScrollUp.startAnimation(outAnimDelay)
                            fabScrollUp.postOnAnimation {
                                fabScrollUp.visibility = View.INVISIBLE
                            }
                        }
                    }
                    else {
                        if(fabScrollUp.visibility == View.INVISIBLE) {
                            fabScrollUp.startAnimation(inAnim)
                            fabScrollUp.postOnAnimation {
                                fabScrollUp.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        })
    }

    private fun closeAllFAB(
        context: Context,
        fabOption: FloatingActionButton,
        buttons: List<FloatingActionButton>,
        textViews: List<TextView>
    ) {
        val outAnim = AnimationUtils.loadAnimation(context, R.anim.zoom_out)
        fabOption.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                R.drawable.ic_camera
            )
        )
        buttons.forEach { bt ->
            bt.startAnimation(outAnim)
            bt.postOnAnimation {
                bt.visibility = View.INVISIBLE
            }
        }
        textViews.forEach {
            it.startAnimation(outAnim)
            it.postOnAnimation {
                it.visibility = View.INVISIBLE
            }
        }
    }

    private fun openAllFAB(
        context: Context,
        fabOption: FloatingActionButton,
        buttons: List<FloatingActionButton>,
        textViews: List<TextView>
    ) {
        val inAnim = AnimationUtils.loadAnimation(context, R.anim.zoom_in)
        fabOption.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                R.drawable.ic_clear
            )
        )
        buttons.forEach { bt ->
            bt.startAnimation(inAnim)
            bt.postOnAnimation {
                bt.visibility = View.VISIBLE
            }
        }
        textViews.forEach {
            it.startAnimation(inAnim)
            it.postOnAnimation {
                it.visibility = View.VISIBLE
            }
        }
    }
}
