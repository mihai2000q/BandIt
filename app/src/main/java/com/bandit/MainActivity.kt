package com.bandit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bandit.constant.Constants
import com.bandit.databinding.ActivityMainBinding
import com.bandit.di.DILocator
import com.bandit.util.AndroidUtils

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launchWhenCreated {
            DILocator.getDatabase().init()
        }

        val bottomNavView = binding.mainBottomNavigationView
        bottomNavView.selectedItemId = R.id.navigation_home //solving small issue by setting a default

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_nav_host) as NavHostFragment
        val navController = navHostFragment.navController

        setSupportActionBar(binding.mainToolbar)
        setupActionBarWithNavController(navController)
        bottomNavView.setupWithNavController(navController)
        binding.mainDrawerMenu.setupWithNavController(navController)
        setupNavigationElements(navController)

        authentication(navController)
    }

    private fun setupNavigationElements(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_todolist,
                R.id.navigation_login,
                R.id.navigation_signup -> binding.mainBottomNavigationView.visibility = View.GONE
                else -> binding.mainBottomNavigationView.visibility = View.VISIBLE
            }
        }
    }

    private fun authentication(navController: NavController) {
        if(AndroidUtils.getBooleanPreference(this, Constants.Preferences.REMEMBER_ME)
            && DILocator.getAuthenticator().currentUser != null) {
            navController.navigate(R.id.action_loginFragment_to_homeFragment)
        }
        else {
            binding.mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            binding.mainBottomNavigationView.visibility = View.INVISIBLE
        }
    }
}