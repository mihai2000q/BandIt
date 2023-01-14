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
import com.bandit.databinding.ActivityMainBinding
import com.bandit.di.DILocator

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        val bottomNavView = binding.mainBottomNavigationView
        bottomNavView.visibility = View.INVISIBLE
        bottomNavView.selectedItemId = R.id.navigation_home //solving small issue by setting a default

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_nav_host) as NavHostFragment
        val navController = navHostFragment.navController

        setSupportActionBar(binding.mainToolbar)
        setupActionBarWithNavController(navController)
        bottomNavView.setupWithNavController(navController)
        binding.mainDrawerMenu.setupWithNavController(navController)
        setupNavigationElements(navController)

        lifecycleScope.launchWhenCreated {
            DILocator.getDatabase().init()
        }
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
}