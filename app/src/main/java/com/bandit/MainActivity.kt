package com.bandit

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bandit.constant.Constants
import com.bandit.databinding.ActivityMainBinding
import com.bandit.di.DILocator
import com.bandit.service.IPreferencesService
import com.bandit.util.AndroidUtils
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferencesService: IPreferencesService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesService = DILocator.getPreferencesService(this)
        lifecycleScope.launch {
            val destination = AndroidUtils.loadIntent(this@MainActivity) { startApp() }
            whenStarted {
                if(destination == true)
                    findNavController(R.id.main_nav_host).navigate(R.id.action_loginFragment_to_homeFragment)
                else if(destination == null) {
                    val navHostFragment = supportFragmentManager
                        .findFragmentById(R.id.main_nav_host) as NavHostFragment
                    navHostFragment.navController.setGraph(
                        R.navigation.mobile_navigation,
                        bundleOf(
                        Constants.SafeArgs.FAIL_LOGIN_NETWORK to true
                        )
                    )
                }
            }
        }
    }

    private suspend fun startApp(): Boolean? {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavView = binding.mainBottomNavigationView
        bottomNavView.selectedItemId =
            R.id.navigation_home //solving small issue by setting a default

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_nav_host) as NavHostFragment
        val navController = navHostFragment.navController

        setSupportActionBar(binding.mainToolbar)
        setupActionBarWithNavController(navController)
        bottomNavView.setupWithNavController(navController)
        binding.mainDrawerMenu.setupWithNavController(navController)
        setupNavigationElements(navController)
        supportActionBar?.hide()

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
        }
    }

    private suspend fun authentication(): Boolean? {
        return if(preferencesService.getBooleanPreference(Constants.Preferences.REMEMBER_ME)
            && DILocator.getAuthenticator().currentUser != null
        ) {
            if(!AndroidUtils.isNetworkAvailable()) {
                AndroidUtils.lockNavigation(
                    binding.mainBottomNavigationView,
                    binding.mainDrawerLayout
                )
                return null
            }
            DILocator.getDatabase().init()
            true
        } else {
            AndroidUtils.lockNavigation(
                binding.mainBottomNavigationView,
                binding.mainDrawerLayout
            )
            false
        }
    }
}