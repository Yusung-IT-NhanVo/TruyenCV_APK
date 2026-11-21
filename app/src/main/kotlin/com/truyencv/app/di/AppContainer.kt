package com.truyencv.app.di

import android.content.Context
import androidx.room.Room
import com.truyencv.app.data.local.db.TruyenCVDatabase
import com.truyencv.app.data.remote.mottruyen.MotTruyenApiService
import com.truyencv.app.data.remote.truyencv.TruyenCvApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

interface AppContainer {
    val database: TruyenCVDatabase
    val truyenCvApi: TruyenCvApiService
    val motTruyenApi: MotTruyenApiService
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()
    }

    override val database: TruyenCVDatabase by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            TruyenCVDatabase::class.java,
            "truyencv.db"
        ).fallbackToDestructiveMigration(true).build()
    }

    override val truyenCvApi: TruyenCvApiService by lazy {
        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            .baseUrl("https://truyencv.io/wp-json/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(TruyenCvApiService::class.java)
    }

    override val motTruyenApi: MotTruyenApiService by lazy {
        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            .baseUrl("http://api.mottruyen.com/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(MotTruyenApiService::class.java)
    }
}
