package com.bignerdranch.android.playlistmaker.settings.data

import android.content.Context
import com.bignerdranch.android.playlistmaker.PlaylistApplication
import com.bignerdranch.android.playlistmaker.search.data.StorageClient
import com.bignerdranch.android.playlistmaker.settings.domain.api.SettingsRepository
import com.bignerdranch.android.playlistmaker.settings.domain.model.ThemeSettings

class SettingsRepositoryImpl(private val storage: StorageClient<ThemeSettings>, val context: Context) : SettingsRepository {
    override fun getThemeSettings(): ThemeSettings? {
        return storage.getData()
    }

    override fun updateThemeSetting(themeSettings: ThemeSettings) {
        storage.storeData(themeSettings)
        (context as PlaylistApplication).switchTheme(themeSettings.isNight())
    }
}