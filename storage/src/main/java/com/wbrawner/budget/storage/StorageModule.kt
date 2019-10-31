package com.wbrawner.budget.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

const val ENCRYPTED_SHARED_PREFS_FILE_NAME = "shared-prefs.encrypted"

@Module
class StorageModule {
    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferences =
            EncryptedSharedPreferences.create(
                    ENCRYPTED_SHARED_PREFS_FILE_NAME,
                    "budget",
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
}