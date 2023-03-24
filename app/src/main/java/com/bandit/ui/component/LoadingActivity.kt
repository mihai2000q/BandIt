package com.bandit.ui.component

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.bandit.R

class LoadingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)
        finish.observe(this) { if(it) finish() }
    }

    companion object {
        val finish = MutableLiveData<Boolean>()
    }
}