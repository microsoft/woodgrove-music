package com.woodgrove.android.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.microsoft.identity.client.IAccount
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.exception.MsalException
import com.woodgrove.android.databinding.ActivityLaunchBinding
import com.woodgrove.android.utils.AuthClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LaunchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLaunchBinding
    private lateinit var authClient: ISingleAccountPublicClientApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        CoroutineScope(Dispatchers.Main).launch {
            authClient = AuthClient.getAuthClient()
            authClient.getCurrentAccountAsync(currentAccountCallback())
        }
    }

    private fun currentAccountCallback(): ISingleAccountPublicClientApplication.CurrentAccountCallback {
        return object : ISingleAccountPublicClientApplication.CurrentAccountCallback {
            override fun onAccountLoaded(activeAccount: IAccount?) {
                if (activeAccount != null) {
                    startActivity(HomeActivity.getStartIntent(this@LaunchActivity))
                } else {
                    startActivity(LandingActivity.getStartIntent(this@LaunchActivity))
                }
            }

            override fun onAccountChanged(priorAccount: IAccount?, currentAccount: IAccount?) {
            }

            override fun onError(exception: MsalException) {
            }
        }
    }
}