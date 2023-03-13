package com.bandit.ui.account

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bandit.databinding.ActivityAccountBinding

class AccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
    }
}