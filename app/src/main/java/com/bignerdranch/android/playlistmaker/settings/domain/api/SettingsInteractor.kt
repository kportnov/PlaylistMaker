package com.bignerdranch.android.playlistmaker.settings.domain.api

import com.bignerdranch.android.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsInteractor {
    fun getThemeSettings(): ThemeSettings?
    fun updateThemeSetting(settings: ThemeSettings)
}