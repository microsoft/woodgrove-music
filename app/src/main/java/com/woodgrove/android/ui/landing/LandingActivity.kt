package com.woodgrove.android.ui.landing


import android.animation.ArgbEvaluator
import android.animation.TimeAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.woodgrove.android.R
import com.woodgrove.android.databinding.ActivityLandingBinding

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

        val start = getColor(R.color.colorGradientStart)
        val mid = getColor(R.color.colorGradientCenter)
        val end = getColor(R.color.colorGradientEnd)

        val gradient = binding.root.background as GradientDrawable

        val evaluator = ArgbEvaluator()
        val animator = TimeAnimator.ofFloat(0.0f, 1.0f)
        animator.duration = 1500
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.addUpdateListener {
            val fraction = it.animatedFraction
            val newStart = evaluator.evaluate(fraction, start, end) as Int
            val newMid = evaluator.evaluate(fraction, mid, start) as Int
            val newEnd = evaluator.evaluate(fraction, end, mid) as Int

            gradient.colors = intArrayOf(newStart, newMid, newEnd)
        }

        animator.start()
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