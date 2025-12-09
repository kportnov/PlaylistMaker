package com.bignerdranch.android.playlistmaker.settings.domain.impl

import com.bignerdranch.android.playlistmaker.settings.domain.api.SettingsInteractor
import com.bignerdranch.android.playlistmaker.settings.domain.api.SettingsRepository
import com.bignerdranch.android.playlistmaker.settings.domain.model.ThemeSettings

class SettingsInteractorImpl (private val settingsRepository: SettingsRepository): SettingsInteractor {
    override fun getThemeSettings(): ThemeSettings? {
        return settingsRepository.getThemeSettings()
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        settingsRepository.updateThemeSetting(settings)
    }
}