package com.woodgrove.android.utils

import android.content.Context
import android.util.Log
import com.microsoft.identity.client.INativeAuthPublicClientApplication
import com.microsoft.identity.client.Logger
import com.microsoft.identity.client.PublicClientApplication
import com.woodgrove.android.R

object AuthClient {
    private lateinit var authClient: INativeAuthPublicClientApplication

    fun getAuthClient(): INativeAuthPublicClientApplication {
        return authClient
    }

    fun initialize(context: Context) {
        Logger.getInstance().setExternalLogger { tag, logLevel, message, containsPII ->
            Log.e(
                "MSAL",
                "$tag $logLevel $message"
            )
        }

        authClient = PublicClientApplication.createNativeAuthPublicClientApplication(
            context,
            R.raw.msal_app_config
        )
    }
}