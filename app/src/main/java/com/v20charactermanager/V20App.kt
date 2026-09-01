package com.v20charactermanager

import android.app.Application
import com.v20charactermanager.data.di.AppContainer

class V20App : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
