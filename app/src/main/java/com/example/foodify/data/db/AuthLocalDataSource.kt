package com.example.foodify.data.db

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class AuthLocalDataSource(context: Context) {

    companion object {
        private const val PREF_NAME = "secure_prefs"
        const val KEY_IS_LOGGED_IN = "is_logged_in"
        const val KEY_IS_GUEST = "is_guest"
    }

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREF_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun setLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun saveGuestMode(isGuest: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_GUEST, isGuest).apply()
    }
    fun isGuestMode(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_GUEST, false)
    }



    fun clearUserData() {
        sharedPreferences.edit().clear().apply()
    }
}
