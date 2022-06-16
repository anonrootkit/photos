@file:Suppress("unused")

package com.fiore.photos.data.sources.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.fiore.photos.utils.PreferenceKeys.tempUserIdKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class AuthPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    suspend fun setTempUserId(userId : String) =
        dataStore.edit { it[tempUserIdKey] = userId }

    fun getTempUserId(): String? =
        runBlocking { dataStore.data.map { it[tempUserIdKey] }.first() }

    suspend fun resetTempUserId() = dataStore.edit { it[tempUserIdKey] = "" }
}