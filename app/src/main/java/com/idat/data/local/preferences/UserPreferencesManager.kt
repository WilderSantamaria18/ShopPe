package com.idat.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesManager @Inject constructor(
    private val context: Context
) {
    companion object {
        val THEME_KEY = booleanPreferencesKey("is_dark_theme")
        val VIEW_MODE_KEY = stringPreferencesKey("view_mode") // "grid" o "list"
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val USER_PHOTO_KEY = stringPreferencesKey("user_photo")
    }

    val isDarkTheme: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[THEME_KEY] ?: false
        }

    val viewMode: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[VIEW_MODE_KEY] ?: "grid"
        }

    val userName: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[USER_NAME_KEY] ?: ""
        }

    val userPhoto: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[USER_PHOTO_KEY] ?: ""
        }

    suspend fun setDarkTheme(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = isDark
        }
    }

    suspend fun setViewMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[VIEW_MODE_KEY] = mode
        }
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = name
        }
    }

    suspend fun setUserPhoto(photoUrl: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_PHOTO_KEY] = photoUrl
        }
    }
}
