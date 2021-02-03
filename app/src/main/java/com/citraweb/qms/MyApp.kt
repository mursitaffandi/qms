package com.citraweb.qms

import android.app.Application
import timber.log.Timber
import timber.log.Timber.DebugTree


class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this

        // This will initialise Timber
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }

    companion object {
        lateinit var instance: MyApp
            private set
    }
}