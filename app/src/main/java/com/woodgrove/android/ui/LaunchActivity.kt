package com.woodgrove.android.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.microsoft.identity.nativeauth.statemachine.results.GetAccountResult
import com.woodgrove.android.databinding.ActivityLaunchBinding
import com.woodgrove.android.utils.AuthClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LaunchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLaunchBinding
    private val authClient = AuthClient.getAuthClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }


    override fun onStart() {
        super.onStart()

        CoroutineScope(Dispatchers.Main).launch {
            if (containsAccount()) {
                startActivity(HomeActivity.getStartIntent(this@LaunchActivity))
            } else {
                startActivity(LandingActivity.getStartIntent(this@LaunchActivity))
            }
            finish()
        }
    }

    private suspend fun containsAccount(): Boolean {
        val accountResult = authClient.getCurrentAccount()
        return when (accountResult) {
            is GetAccountResult.AccountFound -> true
            is GetAccountResult.NoAccountFound -> false
            else -> false // Default case
        }
    }
}