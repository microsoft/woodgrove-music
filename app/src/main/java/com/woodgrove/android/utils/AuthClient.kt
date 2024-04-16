package com.woodgrove.android.utils

import android.content.Context
import android.util.Log
import com.microsoft.identity.client.Logger
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.nativeauth.INativeAuthPublicClientApplication
import com.woodgrove.android.R

object AuthClient {
    private lateinit var authClient: INativeAuthPublicClientApplication

    fun getAuthClient(): INativeAuthPublicClientApplication {
        return authClient
    }

    fun initialize(context: Context) {
        authClient = PublicClientApplication.createNativeAuthPublicClientApplication(
            context,
            R.raw.msal_app_config
        )
    }
}