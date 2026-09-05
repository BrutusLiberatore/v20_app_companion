package com.v20charactermanager

import android.app.Application
import com.v20charactermanager.util.CrashHandler

class V20App : Application() {
    override fun onCreate() {
        super.onCreate()
        CrashHandler.init(this) { _ ->
            // Crash logged to file, can be viewed from Settings
        }
    }
}
