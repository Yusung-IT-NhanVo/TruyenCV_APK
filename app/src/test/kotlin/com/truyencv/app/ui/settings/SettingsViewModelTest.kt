package com.truyencv.app.ui.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.truyencv.app.data.local.datastore.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeDataStore: FakeSettingsDataStore
    private lateinit var appPreferences: AppPreferences

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeDataStore = FakeSettingsDataStore()
        appPreferences = AppPreferences(fakeDataStore)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testSetThemeMode_updatesPreference() = runTest(testDispatcher) {
        appPreferences.setThemeMode(AppPreferences.THEME_DARK)
        val theme = appPreferences.themeMode.first()
        assertEquals(AppPreferences.THEME_DARK, theme)
    }

    @Test
    fun testSetDefaultSource_updatesPreference() = runTest(testDispatcher) {
        appPreferences.setDefaultSource(AppPreferences.SOURCE_MOTTRUYEN)
        val source = appPreferences.defaultSource.first()
        assertEquals(AppPreferences.SOURCE_MOTTRUYEN, source)
    }

    @Test
    fun testSetPrefetchBufferSize_updatesPreference() = runTest(testDispatcher) {
        appPreferences.setPrefetchBufferSize(10)
        val size = appPreferences.prefetchBufferSize.first()
        assertEquals(10, size)
    }

    @Test
    fun testSetLastBackupTime_updatesPreference() = runTest(testDispatcher) {
        val now = 1700000000000L
        appPreferences.setLastBackupTime(now)
        val time = appPreferences.lastBackupTime.first()
        assertEquals(now, time)
    }

    private class FakeSettingsDataStore(
        initialPreferences: Preferences = emptyPreferences()
    ) : DataStore<Preferences> {
        private val flow = MutableStateFlow(initialPreferences)
        override val data: Flow<Preferences> = flow

        override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
            val updated = transform(flow.value)
            flow.value = updated
            return updated
        }
    }
}
