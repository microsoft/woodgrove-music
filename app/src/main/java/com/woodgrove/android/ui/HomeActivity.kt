package com.woodgrove.android.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.microsoft.identity.nativeauth.statemachine.results.GetAccountResult
import com.microsoft.identity.nativeauth.statemachine.results.SignOutResult
import com.woodgrove.android.databinding.ActivityHomeBinding
import com.woodgrove.android.utils.AuthClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeActivity : Activity() {

    private lateinit var binding: ActivityHomeBinding

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, HomeActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
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
        binding.woodgroveHome.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val getAccountResult = AuthClient.getAuthClient().getCurrentAccount()
                if (getAccountResult is GetAccountResult.AccountFound) {
                    val signOutResult = getAccountResult.resultValue.signOut()
                    if (signOutResult is SignOutResult.Complete) {
                        startActivity(LandingActivity.getStartIntent(this@HomeActivity))
                    }
                }
            }
        }
    }

    private fun navigateToProfile() {
        startActivity(ProfileActivity.getStartIntent(this))
    }
}