package com.bandit

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bandit.constant.Constants
import com.bandit.databinding.ActivityMainBinding
import com.bandit.di.DILocator
import com.bandit.service.IPreferencesService
import com.bandit.ui.account.AccountActivity
import com.bandit.ui.account.AccountViewModel
import com.bandit.ui.component.AndroidComponents
import com.bandit.util.AndroidUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferencesService: IPreferencesService
    private lateinit var accountActivityLauncher: ActivityResultLauncher<Intent>
    private val accountViewModel: AccountViewModel by viewModels()
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.setContentView(binding.root)
        preferencesService = DILocator.getPreferencesService(this)
        accountActivityLauncher = this.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if(it.resultCode == Activity.RESULT_OK) {
                lifecycleScope.launch {
                    val result = AndroidUtils.loadIntent(this@MainActivity) {
                        this@MainActivity.updateAccount(it.data?.extras!!)
                    }
                    if (result == true)
                        AndroidComponents.toastNotification(
                            applicationContext,
                            resources.getString(R.string.account_updated_toast)
                        )
                }
            }
            if(it.resultCode == Constants.Account.RESULT_SIGN_OUT)
                this.signOut()
        }
        lifecycleScope.launch {
            val destination = AndroidUtils.loadIntent(
                this@MainActivity) { startApp() }
            this@MainActivity.whenStarted {
                if(destination == true)
                    findNavController(R.id.main_nav_host)
                        .navigate(R.id.action_loginFragment_to_homeFragment)
                else if(destination == null) {
                    val navHostFragment = supportFragmentManager
                        .findFragmentById(R.id.main_nav_host) as NavHostFragment
                    navHostFragment.navController.setGraph(
                        R.navigation.mobile_navigation,
                        bundleOf(Constants.SafeArgs.FAIL_LOGIN_NETWORK to true)
                    )
                }
            }
        }
    }

    private suspend fun startApp(): Boolean? {
        val bottomNavView = binding.mainBottomNavigationView
        // solving small issue by setting a default
        bottomNavView.selectedItemId = R.id.navigation_home

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_nav_host) as NavHostFragment
        val navController = navHostFragment.navController

        super.setSupportActionBar(binding.mainToolbar)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_concerts, R.id.navigation_songs,
                R.id.navigation_social, R.id.navigation_schedule
            ), binding.mainDrawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavView.setupWithNavController(navController)
        binding.mainDrawerMenu.setupWithNavController(navController)
        this.setupNavigationElements(navController)
        this.setupSwipeRefreshLayout(binding.swipeRefreshLayout, navController)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        return authentication()
    }

    private fun setupNavigationElements(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home,
                R.id.navigation_concerts,
                R.id.navigation_songs,
                R.id.navigation_social,
                R.id.navigation_schedule -> binding.mainBottomNavigationView.visibility = View.VISIBLE
                else -> binding.mainBottomNavigationView.visibility = View.GONE
            }
            when(destination.id) {
                R.id.navigation_signup,
                R.id.navigation_login,
                R.id.navigation_first_login -> binding.swipeRefreshLayout.isEnabled = false
                else -> binding.swipeRefreshLayout.isEnabled = true
            }
        }
    }

    private fun setupSwipeRefreshLayout(swipeRefreshLayout: SwipeRefreshLayout, navController: NavController) {
        swipeRefreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
                viewModelStore.clear()
                DILocator.getDatabase().clearData()
                DILocator.getDatabase().init(DILocator.getAuthenticator().currentUser!!.uid)
                AndroidComponents.toastNotification(
                    applicationContext,
                    resources.getString(R.string.page_refresh_toast)
                )
                AndroidUtils.refreshFragment(navController)
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private suspend fun authentication(): Boolean? {
        return if(
            preferencesService.getBooleanPreference(Constants.Preferences.REMEMBER_ME) &&
            DILocator.getAuthenticator().currentUser != null
        ) {
            if(!AndroidUtils.isNetworkAvailable()) {
                AndroidUtils.lockNavigation(this)
                return null
            }
            DILocator.getDatabase().init(DILocator.getAuthenticator().currentUser!!.uid)
            true
        } else {
            AndroidUtils.lockNavigation(this)
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private suspend fun updateAccount(extras: Bundle): Boolean = coroutineScope {
        async {
            val newAccount = extras.getParcelable(
                Constants.Account.RESULT_ACCOUNT_EXTRA,
                com.bandit.data.model.Account::class.java
            )!!
            if (!newAccount.isEmpty()) {
                launch { accountViewModel.updateAccount(newAccount) }
            }
            val res = extras.getBoolean(Constants.Account.RESULT_PROFILE_PIC_CHANGED_EXTRA)
            if (res)
                launch {
                    accountViewModel.saveProfilePicture(
                        extras.getParcelable(
                            Constants.Account.RESULT_PROFILE_PIC_EXTRA,
                            android.net.Uri::class.java
                        )!!
                    )
                }
            if(!res && (newAccount.isEmpty() || newAccount == accountViewModel.account.value!!))
                return@async false
            return@async true
        }
    }.await()

    private fun signOut() {
        accountViewModel.signOut()
        //go back to login fragment
        val navController = this.findNavController(R.id.main_nav_host)
        for(i in 0 until navController.backQueue.size)
            navController.popBackStack()
        navController.navigate(R.id.navigation_login)
        AndroidUtils.lockNavigation(this)
        preferencesService.resetAllPreferences()
        this.viewModelStore.clear()
        AndroidComponents.toastNotification(
            applicationContext,
            resources.getString(R.string.sign_out_toast),
            Toast.LENGTH_LONG
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        DILocator.getDatabase().clearData()
        viewModelStore.clear()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val accountButton = menu?.findItem(R.id.action_bar_profile)
        accountButton?.isVisible = isAccountButtonShown
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_bar_profile -> {
                lifecycleScope.launch {
                    val accountIntent = Intent(this@MainActivity, AccountActivity::class.java)
                    accountIntent.putExtra(
                        Constants.Account.EXTRA,
                        accountViewModel.account.value!!
                    )
                    val profilePic = accountViewModel.getProfilePicture()
                    accountIntent.putExtra(Constants.Account.PROFILE_PIC_EXTRA, profilePic)
                    accountActivityLauncher.launch(accountIntent)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return if(!binding.mainDrawerLayout.isOpen &&
            binding.mainDrawerLayout
                .getDrawerLockMode(binding.mainDrawerMenu) != DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        ) {
            binding.mainDrawerLayout.open()
            true
        }
        else
            false
    }
    companion object {
        var isAccountButtonShown = true
    }
}