package com.bandit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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
        val bottomNavView = binding.bottomNavigationView
        bottomNavView.visibility = View.INVISIBLE
        bottomNavView.selectedItemId = R.id.navigation_home //solving small issue by setting a default

        val navController = findNavController(R.id.fragment_container_view)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_concerts, R.id.navigation_songs, R.id.navigation_home,
                R.id.navigation_chats, R.id.navigation_schedule
            )
        )
        Log.i("test", "IT MADE THE VARIABLES")
        setupActionBarWithNavController(navController, appBarConfiguration)
        Log.i("test", "IT SETUP ")
        bottomNavView.setupWithNavController(navController)
    }
}