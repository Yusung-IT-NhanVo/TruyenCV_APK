package com.truyencv.app.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.readerDataStore: DataStore<Preferences> by preferencesDataStore(name = "reader_preferences")

class ReaderPreferences(private val dataStore: DataStore<Preferences>) {

    constructor(context: Context) : this(context.readerDataStore)

    companion object {
        val KEY_FONT_SIZE = floatPreferencesKey("font_size")
        val KEY_FONT_FAMILY = stringPreferencesKey("font_family")
        val KEY_LINE_HEIGHT = floatPreferencesKey("line_height")
        val KEY_BG_COLOR = stringPreferencesKey("background_color")
        val KEY_TEXT_COLOR = stringPreferencesKey("text_color")
        val KEY_READER_MODE = stringPreferencesKey("reader_mode")
        val KEY_KEEP_SCREEN_ON = booleanPreferencesKey("keep_screen_on")
        val KEY_VOLUME_KEY_NAV = booleanPreferencesKey("volume_key_nav")
        val KEY_TTS_SPEED = floatPreferencesKey("tts_speed")
        val KEY_TTS_PITCH = floatPreferencesKey("tts_pitch")

        const val DEFAULT_FONT_SIZE = 18f
        const val DEFAULT_FONT_FAMILY = "Roboto"
        const val DEFAULT_LINE_HEIGHT = 1.5f
        const val DEFAULT_BG_COLOR = "#FFFFFF"
        const val DEFAULT_TEXT_COLOR = "#212121"
        const val DEFAULT_READER_MODE = "SCROLL"
        const val DEFAULT_KEEP_SCREEN_ON = true
        const val DEFAULT_VOLUME_KEY_NAV = false
        const val DEFAULT_TTS_SPEED = 1.0f
        const val DEFAULT_TTS_PITCH = 1.0f
    }

    val fontSize: Flow<Float> = dataStore.data.map { it[KEY_FONT_SIZE] ?: DEFAULT_FONT_SIZE }
    val fontFamily: Flow<String> = dataStore.data.map { it[KEY_FONT_FAMILY] ?: DEFAULT_FONT_FAMILY }
    val lineHeight: Flow<Float> = dataStore.data.map { it[KEY_LINE_HEIGHT] ?: DEFAULT_LINE_HEIGHT }
    val backgroundColor: Flow<String> = dataStore.data.map { it[KEY_BG_COLOR] ?: DEFAULT_BG_COLOR }
    val textColor: Flow<String> = dataStore.data.map { it[KEY_TEXT_COLOR] ?: DEFAULT_TEXT_COLOR }
    val readerMode: Flow<String> = dataStore.data.map { it[KEY_READER_MODE] ?: DEFAULT_READER_MODE }
    val keepScreenOn: Flow<Boolean> = dataStore.data.map { it[KEY_KEEP_SCREEN_ON] ?: DEFAULT_KEEP_SCREEN_ON }
    val volumeKeyNavigation: Flow<Boolean> = dataStore.data.map { it[KEY_VOLUME_KEY_NAV] ?: DEFAULT_VOLUME_KEY_NAV }
    val ttsSpeed: Flow<Float> = dataStore.data.map { it[KEY_TTS_SPEED] ?: DEFAULT_TTS_SPEED }
    val ttsPitch: Flow<Float> = dataStore.data.map { it[KEY_TTS_PITCH] ?: DEFAULT_TTS_PITCH }

    suspend fun setFontSize(size: Float) {
        dataStore.edit { it[KEY_FONT_SIZE] = size }
    }

    suspend fun setFontFamily(family: String) {
        dataStore.edit { it[KEY_FONT_FAMILY] = family }
    }

    suspend fun setLineHeight(height: Float) {
        dataStore.edit { it[KEY_LINE_HEIGHT] = height }
    }

    suspend fun setBackgroundColor(colorHex: String) {
        dataStore.edit { it[KEY_BG_COLOR] = colorHex }
    }

    suspend fun setTextColor(colorHex: String) {
        dataStore.edit { it[KEY_TEXT_COLOR] = colorHex }
    }

    suspend fun setReaderMode(mode: String) {
        dataStore.edit { it[KEY_READER_MODE] = mode }
    }

    suspend fun setKeepScreenOn(enabled: Boolean) {
        dataStore.edit { it[KEY_KEEP_SCREEN_ON] = enabled }
    }

    suspend fun setVolumeKeyNavigation(enabled: Boolean) {
        dataStore.edit { it[KEY_VOLUME_KEY_NAV] = enabled }
    }

    suspend fun setTtsSpeed(speed: Float) {
        dataStore.edit { it[KEY_TTS_SPEED] = speed }
    }

    suspend fun setTtsPitch(pitch: Float) {
        dataStore.edit { it[KEY_TTS_PITCH] = pitch }
    }
}
