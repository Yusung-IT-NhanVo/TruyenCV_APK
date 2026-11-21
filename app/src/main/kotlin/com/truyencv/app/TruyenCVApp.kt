package com.truyencv.app

import android.app.Application
import com.truyencv.app.di.AppContainer
import com.truyencv.app.di.DefaultAppContainer

class TruyenCVApp : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
