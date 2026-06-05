package com.fios.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

import com.fios.app.ui.theme.FIOSThemeType

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "fios_settings")

data class UserPreferences(
    val theme: FIOSThemeType,
    val username: String,
    val isBiometricEnabled: Boolean,
    val isIncognitoEnabled: Boolean,
    val cpuOverloadThreshold: Float
)

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val THEME = stringPreferencesKey("fios_theme")
        val USERNAME = stringPreferencesKey("fios_username")
        val BIOMETRIC = booleanPreferencesKey("fios_biometric")
        val INCOGNITO = booleanPreferencesKey("fios_incognito")
        val CPU_THRESHOLD = floatPreferencesKey("fios_cpu_threshold")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val themeName = preferences[PreferencesKeys.THEME] ?: FIOSThemeType.HACKER_GREEN.name
            val theme = try { FIOSThemeType.valueOf(themeName) } catch (e: Exception) { FIOSThemeType.HACKER_GREEN }
            
            UserPreferences(
                theme = theme,
                username = preferences[PreferencesKeys.USERNAME] ?: "OPERADOR",
                isBiometricEnabled = preferences[PreferencesKeys.BIOMETRIC] ?: false,
                isIncognitoEnabled = preferences[PreferencesKeys.INCOGNITO] ?: false,
                cpuOverloadThreshold = preferences[PreferencesKeys.CPU_THRESHOLD] ?: 0.8f
            )
        }

    suspend fun updateTheme(theme: FIOSThemeType) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme.name
        }
    }

    suspend fun updateUsername(username: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USERNAME] = username
        }
    }

    suspend fun updateBiometric(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BIOMETRIC] = enabled
        }
    }

    suspend fun updateIncognito(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.INCOGNITO] = enabled
        }
    }
}
