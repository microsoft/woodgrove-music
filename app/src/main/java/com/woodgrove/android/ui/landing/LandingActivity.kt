package com.woodgrove.android.ui.landing

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.woodgrove.android.R
import com.woodgrove.android.databinding.ActivityLandingBinding
import com.woodgrove.android.utils.AuthClient

class LandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingBinding

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, LandingActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onStart() {
        super.onStart()
        initializeLandingListeners()
    }

    private fun initializeLandingListeners() {
        binding.landingSignup.setOnClickListener {
            openSignupOverlay()
        }
    }

    private fun openSignupOverlay() {
        startActivity(SignupActivity.getStartIntent(this))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}