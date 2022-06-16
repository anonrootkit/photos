package com.fiore.photos.utils

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import java.util.concurrent.TimeUnit

object PreferenceKeys {
    val tempUserIdKey : Preferences.Key<String> = stringPreferencesKey("tempUserId")
}

object TimeConstants {
    val ONE_SECOND = TimeUnit.SECONDS.toMillis(1L)
}