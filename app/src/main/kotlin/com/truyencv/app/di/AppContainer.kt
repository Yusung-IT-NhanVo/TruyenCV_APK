package com.truyencv.app.di

import android.content.Context
import androidx.room.Room
import com.truyencv.app.data.local.datastore.AppPreferences
import com.truyencv.app.data.local.datastore.ReaderPreferences
import com.truyencv.app.data.local.db.TruyenCVDatabase
import com.truyencv.app.data.remote.interceptor.MotTruyenDefaultParamsInterceptor
import com.truyencv.app.data.remote.interceptor.UserAgentInterceptor
import com.truyencv.app.data.remote.mottruyen.MotTruyenApiService
import com.truyencv.app.data.remote.truyencv.TruyenCvApiService
import com.truyencv.app.data.repository.BookmarkRepositoryImpl
import com.truyencv.app.data.repository.ChapterRepositoryImpl
import com.truyencv.app.data.repository.HistoryRepositoryImpl
import com.truyencv.app.data.repository.NovelRepositoryImpl
import com.truyencv.app.domain.repository.BookmarkRepository
import com.truyencv.app.domain.repository.ChapterRepository
import com.truyencv.app.domain.repository.HistoryRepository
import com.truyencv.app.domain.repository.NovelRepository
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
    val novelRepository: NovelRepository
    val chapterRepository: ChapterRepository
    val bookmarkRepository: BookmarkRepository
    val historyRepository: HistoryRepository
    val readerPreferences: ReaderPreferences
    val appPreferences: AppPreferences
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    private val baseOkHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(UserAgentInterceptor())
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()
    }

    private val motTruyenOkHttpClient: OkHttpClient by lazy {
        baseOkHttpClient.newBuilder()
            .addInterceptor(MotTruyenDefaultParamsInterceptor())
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
            .client(baseOkHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(TruyenCvApiService::class.java)
    }

    override val motTruyenApi: MotTruyenApiService by lazy {
        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            .baseUrl("http://api.mottruyen.com/")
            .client(motTruyenOkHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(MotTruyenApiService::class.java)
    }

    override val novelRepository: NovelRepository by lazy {
        NovelRepositoryImpl(
            truyenCvApi = truyenCvApi,
            motTruyenApi = motTruyenApi,
            novelDao = database.novelDao(),
            genreDao = database.genreDao()
        )
    }

    override val chapterRepository: ChapterRepository by lazy {
        ChapterRepositoryImpl(
            truyenCvApi = truyenCvApi,
            motTruyenApi = motTruyenApi,
            chapterDao = database.chapterDao()
        )
    }

    override val bookmarkRepository: BookmarkRepository by lazy {
        BookmarkRepositoryImpl(
            bookmarkDao = database.bookmarkDao(),
            novelDao = database.novelDao()
        )
    }

    override val historyRepository: HistoryRepository by lazy {
        HistoryRepositoryImpl(
            readingHistoryDao = database.readingHistoryDao()
        )
    }

    override val readerPreferences: ReaderPreferences by lazy {
        ReaderPreferences(context.applicationContext)
    }

    override val appPreferences: AppPreferences by lazy {
        AppPreferences(context.applicationContext)
    }
}
