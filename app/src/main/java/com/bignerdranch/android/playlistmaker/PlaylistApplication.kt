package com.bignerdranch.android.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.bignerdranch.android.playlistmaker.di.dataModule
import com.bignerdranch.android.playlistmaker.di.interactorModule
import com.bignerdranch.android.playlistmaker.di.repositoryModule
import com.bignerdranch.android.playlistmaker.di.viewModelModule
import com.bignerdranch.android.playlistmaker.settings.domain.api.SettingsInteractor
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class PlaylistApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@PlaylistApplication)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }
        val settingsInteractor: SettingsInteractor by inject()

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