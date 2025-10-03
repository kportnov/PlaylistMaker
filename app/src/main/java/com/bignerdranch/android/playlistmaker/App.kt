package com.bignerdranch.android.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class App: Application() {


    override fun onCreate() {
        super.onCreate()

        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_SETTINGS, MODE_PRIVATE)
        switchTheme(sharedPreferences.getBoolean(NIGHT_MODE_KEY, false))

    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}