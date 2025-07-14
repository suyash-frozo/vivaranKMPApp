package com.vivaran.app

import android.app.Application
import com.vivaran.shared.di.sharedModule
import com.vivaran.shared.di.platformModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class VivaranApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidContext(this@VivaranApplication)
            modules(sharedModule, platformModule, appModule)
        }
    }
}