package com.truyencv.app

import android.app.Application
import androidx.compose.runtime.compositionLocalOf
import com.truyencv.app.di.AppContainer
import com.truyencv.app.di.DefaultAppContainer

/**
 * CompositionLocal providing [AppContainer] to composables throughout the Compose hierarchy.
 */
val LocalAppContainer = compositionLocalOf<AppContainer> {
    error("No AppContainer provided. Make sure to provide it via CompositionLocalProvider.")
}

/**
 * Base Application class for TruyenCV.
 * Initializes the root dependency injection container and stores singleton references.
 */
class TruyenCVApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        container = DefaultAppContainer(this)
    }

    companion object {
        lateinit var instance: TruyenCVApp
            private set
    }
}
