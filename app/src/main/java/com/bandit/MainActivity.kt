package com.bandit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bandit.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bottomNavView = binding.mainBottomNavigationView
        bottomNavView.visibility = View.INVISIBLE
        bottomNavView.selectedItemId = R.id.navigation_home //solving small issue by setting a default

        val navController = findNavController(R.id.main_nav_host)
        val appBarConfiguration = AppBarConfiguration.Builder(R.id.navigation_concerts, R.id.navigation_songs,
            R.id.navigation_home, R.id.navigation_chats, R.id.navigation_schedule,R.id.navigation_todolist
        ).setOpenableLayout(binding.mainDrawerLayout).build()
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavView.setupWithNavController(navController)
        binding.mainDrawerMenu.setupWithNavController(navController)
        //supportActionBar?.hideOffset
    }
}