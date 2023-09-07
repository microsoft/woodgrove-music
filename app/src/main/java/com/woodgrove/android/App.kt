package com.woodgrove.android

import android.app.Application
import com.woodgrove.android.utils.AuthClient

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialise global MSAL reference
        AuthClient.initialize(this)
    }
}