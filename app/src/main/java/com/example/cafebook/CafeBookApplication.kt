package com.example.cafebook

import android.app.Application
import com.example.cafebook.Utils.NetworkUtils
import com.example.cafebook.di.dataModule
import com.example.cafebook.di.databaseModule
import com.example.cafebook.di.ioDispatcherModule
import com.example.cafebook.di.moshiModule
import com.example.cafebook.di.networkModule
import com.example.cafebook.di.viewModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CafeBookApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NetworkUtils.initialize(this)
        startKoin {
            androidContext(this@CafeBookApplication)
            modules(
                listOf(
                    databaseModule,
                    dataModule,
                    viewModule,
                    ioDispatcherModule,
                    moshiModule,
                    networkModule,
                ),
            )
        }
    }
}
