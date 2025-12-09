package com.bignerdranch.android.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.bignerdranch.android.playlistmaker.creator.Creator


class PlaylistApplication: Application() {


    override fun onCreate() {
        super.onCreate()

        val settingsInteractor = Creator.provideSettingsInteractor(applicationContext)
        switchTheme(settingsInteractor.getThemeSettings()?.isNight() ?: false)

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