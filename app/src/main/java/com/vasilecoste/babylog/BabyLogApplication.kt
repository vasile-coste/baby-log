package com.vasilecoste.babylog

import android.app.Application
import com.vasilecoste.babylog.data.di.AppContainer

class BabyLogApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
