package com.woodgrove.android.ui


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.woodgrove.android.R
import com.woodgrove.android.databinding.ActivityLandingBinding
import com.woodgrove.android.ui.login.LoginActivity
import com.woodgrove.android.ui.signup.SignupActivity

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
            Handler(Looper.getMainLooper()).postDelayed({
                hideLogo()
            }, 100)
        }
        binding.landingLogin.setOnClickListener {
            openLoginOverlay()
        }
    }

    private fun openSignupOverlay() {
        startActivity(SignupActivity.getStartIntent(this))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private fun openLoginOverlay() {
        startActivity(LoginActivity.getStartIntent(this))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    fun hideLogo() {
        binding.landingMainText.visibility = View.GONE
        binding.landingLogo.visibility = View.GONE
    }
}