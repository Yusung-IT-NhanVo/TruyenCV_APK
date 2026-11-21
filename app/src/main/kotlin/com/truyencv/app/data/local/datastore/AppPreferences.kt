package com.truyencv.app.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

class AppPreferences(private val dataStore: DataStore<Preferences>) {

    constructor(context: Context) : this(context.appDataStore)

    companion object {
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        val KEY_DEFAULT_SOURCE = stringPreferencesKey("default_source")
        val KEY_LAST_BACKUP_TIME = longPreferencesKey("last_backup_time")
        val KEY_PREFETCH_BUFFER_SIZE = androidx.datastore.preferences.core.intPreferencesKey("prefetch_buffer_size")

        const val THEME_SYSTEM = "SYSTEM"
        const val THEME_LIGHT = "LIGHT"
        const val THEME_DARK = "DARK"

        const val SOURCE_ALL = "ALL"
        const val SOURCE_TRUYENCV = "TRUYENCV"
        const val SOURCE_MOTTRUYEN = "MOTTRUYEN"
        const val DEFAULT_PREFETCH_BUFFER_SIZE = 6
    }

    val themeMode: Flow<String> = dataStore.data.map { it[KEY_THEME_MODE] ?: THEME_SYSTEM }
    val defaultSource: Flow<String> = dataStore.data.map { it[KEY_DEFAULT_SOURCE] ?: SOURCE_ALL }
    val lastBackupTime: Flow<Long> = dataStore.data.map { it[KEY_LAST_BACKUP_TIME] ?: 0L }
    val prefetchBufferSize: Flow<Int> = dataStore.data.map { it[KEY_PREFETCH_BUFFER_SIZE] ?: DEFAULT_PREFETCH_BUFFER_SIZE }

    suspend fun setThemeMode(mode: String) {
        dataStore.edit { it[KEY_THEME_MODE] = mode }
    }

    suspend fun setDefaultSource(source: String) {
        dataStore.edit { it[KEY_DEFAULT_SOURCE] = source }
    }

    suspend fun setLastBackupTime(time: Long) {
        dataStore.edit { it[KEY_LAST_BACKUP_TIME] = time }
    }

    suspend fun setPrefetchBufferSize(size: Int) {
        dataStore.edit { it[KEY_PREFETCH_BUFFER_SIZE] = size }
    }
}
