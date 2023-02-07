package com.bandit

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
import com.bandit.util.AndroidUtils
import com.bandit.util.PreferencesUtils
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            val destination = AndroidUtils.loadTask(this@MainActivity) { startApp() }
            whenStarted {
                if(destination == true)
                    findNavController(R.id.main_nav_host).navigate(R.id.action_loginFragment_to_homeFragment)
            }
        }
    }

    private suspend fun startApp(): Boolean {
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
                R.id.navigation_todolist,
                R.id.navigation_login,
                R.id.navigation_signup,
                R.id.navigation_first_login -> binding.mainBottomNavigationView.visibility = View.GONE
                else -> binding.mainBottomNavigationView.visibility = View.VISIBLE
            }
        }
    }

    private suspend fun authentication(): Boolean {
        return if(PreferencesUtils.getBooleanPreference(this, Constants.Preferences.REMEMBER_ME)
            && DILocator.authenticator.currentUser != null
        ) {
            DILocator.database.init()
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