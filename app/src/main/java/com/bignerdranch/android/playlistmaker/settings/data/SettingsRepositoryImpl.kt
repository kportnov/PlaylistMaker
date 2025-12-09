package com.bignerdranch.android.playlistmaker.settings.data

import com.bignerdranch.android.playlistmaker.search.data.StorageClient
import com.bignerdranch.android.playlistmaker.settings.domain.api.SettingsRepository
import com.bignerdranch.android.playlistmaker.settings.domain.model.ThemeSettings

class SettingsRepositoryImpl(private val storage: StorageClient<ThemeSettings>) : SettingsRepository {
    override fun getThemeSettings(): ThemeSettings? {
        return storage.getData()
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        storage.storeData(settings)
    }
}