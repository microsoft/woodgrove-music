package com.woodgrove.android.utils

import android.content.Context
import android.util.Log
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.Logger
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.nativeauth.INativeAuthPublicClientApplication
import com.woodgrove.android.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

object AuthClient {
    private lateinit var authClient: Deferred<ISingleAccountPublicClientApplication>


    suspend fun getAuthClient(): ISingleAccountPublicClientApplication {
        return authClient.await()
    }

    fun initialize(context: Context) {
        Logger.getInstance().setExternalLogger { tag, logLevel, message, containsPII ->
            Log.e(
                "MSAL",
                "$tag $logLevel $message"
            )
        }

        authClient = CoroutineScope(Dispatchers.IO).async {
            PublicClientApplication.createSingleAccountPublicClientApplication(
                context,
                R.raw.msal_app_config
            )
        }
    }
}