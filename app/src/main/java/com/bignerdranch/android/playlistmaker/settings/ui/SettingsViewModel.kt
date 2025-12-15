package com.bignerdranch.android.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bignerdranch.android.playlistmaker.settings.domain.api.SettingsInteractor
import com.bignerdranch.android.playlistmaker.settings.domain.api.SharingInteractor
import com.bignerdranch.android.playlistmaker.settings.domain.model.ThemeSettings

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    private val settingsLiveData = MutableLiveData<ThemeSettings>()
    fun observeSettingsLiveData(): LiveData<ThemeSettings> = settingsLiveData

    fun manageTheme(isChecked: Boolean) {
        val theme = if (isChecked) ThemeSettings.NIGHT_MODE else ThemeSettings.DAY_MODE
        settingsInteractor.updateThemeSetting(theme)
    }

    fun getTheme(): Boolean? {
        return settingsInteractor.getThemeSettings()?.isNight()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }
}