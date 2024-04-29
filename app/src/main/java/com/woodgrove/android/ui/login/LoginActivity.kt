package com.woodgrove.android.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.woodgrove.android.R
import com.woodgrove.android.databinding.ActivityLoginBinding
import com.woodgrove.android.utils.Constants

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    companion object {
        fun getStartIntent(context: Context, username: String? = null): Intent {
            return Intent(context, LoginActivity::class.java)
                .putExtra(Constants.Intent.USERNAME, username)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onStart() {
        super.onStart()
        initializeListeners()
    }

    private fun initializeListeners() {
        binding.loginClose.setOnClickListener {
            close()
        }
    }

    private fun close() {
        finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}