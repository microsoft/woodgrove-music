package com.woodgrove.android.ui.landing

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
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

    override fun onResume() {
        super.onResume()
//        showButtons()
    }

    private fun initializeLandingListeners() {
        binding.landingSignup.setOnClickListener {
            openSignupOverlay()
//            hideButtons()
        }
    }

    private fun openSignupOverlay() {
        startActivity(SignupActivity.getStartIntent(this))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private fun showButtons() {
        binding.landingSignup.visibility = View.VISIBLE
        binding.landingLogin.visibility = View.VISIBLE
    }

    private fun hideButtons() {
        binding.landingSignup.visibility = View.GONE
        binding.landingLogin.visibility = View.GONE
    }
}