package com.woodgrove.android.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.woodgrove.android.databinding.ActivityHomeBinding

class HomeActivity : Activity() {

    private lateinit var binding: ActivityHomeBinding

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, HomeActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onStart() {
        super.onStart()
        initializeButtonListeners()
    }

    private fun initializeButtonListeners() {
        binding.homeProfile.setOnClickListener {
            navigateToProfile()
        }
    }

    private fun navigateToProfile() {
        startActivity(ProfileActivity.getStartIntent(this))
    }
}