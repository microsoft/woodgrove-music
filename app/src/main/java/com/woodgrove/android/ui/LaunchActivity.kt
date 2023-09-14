package com.woodgrove.android.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.woodgrove.android.databinding.ActivityLaunchBinding
import com.woodgrove.android.ui.landing.LandingActivity
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
        return authClient.getCurrentAccount() != null
    }
}