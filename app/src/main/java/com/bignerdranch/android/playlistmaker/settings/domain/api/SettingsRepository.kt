package com.bignerdranch.android.playlistmaker.settings.domain.api

import com.bignerdranch.android.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsRepository {
    fun getThemeSettings(): ThemeSettings?
    fun updateThemeSetting(themeSettings: ThemeSettings)
}