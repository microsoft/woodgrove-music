package com.woodgrove.android.ui


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.microsoft.identity.client.AcquireTokenParameters
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.exception.MsalException
import com.woodgrove.android.R
import com.woodgrove.android.databinding.ActivityLandingBinding
import com.woodgrove.android.utils.AuthClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingBinding
    private lateinit var authClient: ISingleAccountPublicClientApplication

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

        CoroutineScope(Dispatchers.Main).launch {
            authClient = AuthClient.getAuthClient()
        }

    }

    override fun onStart() {
        super.onStart()
        initializeLandingListeners()
    }

    override fun onResume() {
        super.onResume()
        showLogo()
    }

    private fun initializeLandingListeners() {
        binding.landingSignup.setOnClickListener {
            openSignupOverlay()
            Handler(Looper.getMainLooper()).postDelayed({
                hideLogo()
            }, 100)
        }
        binding.landingLogin.setOnClickListener {
            acquireTokenInteractively()
        }
    }

    private fun openSignupOverlay() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private fun acquireTokenInteractively() {
        val scopes = "openid offline_access".lowercase().split(" ")
        val parameters = AcquireTokenParameters.Builder()
            .startAuthorizationFromActivity(this@LandingActivity)
            .withScopes(scopes)
            .withCallback(getAuthInteractiveCallback())
            .build()

        authClient.acquireToken(parameters)
    }

    /**
     * Callback used for silent acquireToken calls.
     */
    private fun getAuthInteractiveCallback(): AuthenticationCallback {
        return object : AuthenticationCallback {

            override fun onSuccess(authenticationResult: IAuthenticationResult) {
            }

            override fun onError(exception: MsalException) {
            }

            override fun onCancel() {
            }
        }
    }

    private fun hideLogo() {
        binding.landingMainText.visibility = View.GONE
        binding.landingLogo.visibility = View.GONE
    }

    private fun showLogo() {
        binding.landingMainText.visibility = View.VISIBLE
        binding.landingLogo.visibility = View.VISIBLE
    }
}