package com.bandit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bandit.databinding.ActivityMainBinding
import com.bandit.mock.MockDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        val mockDatabase = MockDatabase()

        val bottomNavView = binding.mainBottomNavigationView
        bottomNavView.visibility = View.INVISIBLE
        bottomNavView.selectedItemId = R.id.navigation_home //solving small issue by setting a default

        val navController = findNavController(R.id.main_nav_host)

        val appBarConfiguration = AppBarConfiguration.Builder(mockDatabase.navigationViewIds
        ).setOpenableLayout(binding.mainDrawerLayout).build()
        setSupportActionBar(binding.mainToolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavView.setupWithNavController(navController)
        binding.mainDrawerMenu.setupWithNavController(navController)
        setupNavigationElements(navController)
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