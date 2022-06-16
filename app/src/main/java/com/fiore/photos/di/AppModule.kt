package com.fiore.photos.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.fiore.photos.BuildConfig
import com.fiore.photos.UserProfile
import com.fiore.photos.data.sources.preferences.serializers.UserProfileSerializer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    private val Context.dataStore by preferencesDataStore(name = BuildConfig.PREFERENCE_NAME)

    private val Context.userProfileStore : DataStore<UserProfile> by dataStore(
        fileName = BuildConfig.PREFERENCE_NAME,
        serializer = UserProfileSerializer
    )

    @Provides
    fun provideDataStore(@ApplicationContext context: Context) : DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    fun provideUserProfileDataStore(@ApplicationContext context: Context) : DataStore<UserProfile> {
        return context.userProfileStore
    }

    @Provides
    fun provideFirebaseAuthInstance() : FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    fun provideFirebaseFireStoreInstance() : FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    fun provideCoroutineScope() : CoroutineScope {
        return CoroutineScope(Dispatchers.IO + Job())
    }

}