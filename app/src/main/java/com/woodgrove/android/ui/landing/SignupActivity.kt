package com.woodgrove.android.ui.landing

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.woodgrove.android.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, SignupActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onStart() {
        super.onStart()
        initializeListeners()
    }

    private fun initializeListeners() {
        binding.signupClose.setOnClickListener {
            finish()
        }
    }
}