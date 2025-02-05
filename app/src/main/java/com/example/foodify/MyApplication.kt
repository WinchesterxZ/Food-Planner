package com.example.foodify

import android.app.Application
import com.example.foodify.di.authModule
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        FacebookSdk.fullyInitialize()
        AppEventsLogger.activateApp(this)
        startKoin {
            androidContext(this@MyApplication)
            modules(authModule)
        }
    }
}