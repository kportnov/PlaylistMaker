package com.bignerdranch.android.playlistmaker.settings.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.bignerdranch.android.playlistmaker.PlaylistApplication
import com.bignerdranch.android.playlistmaker.creator.Creator
import com.bignerdranch.android.playlistmaker.settings.domain.api.SettingsInteractor
import com.bignerdranch.android.playlistmaker.settings.domain.api.SharingInteractor
import com.bignerdranch.android.playlistmaker.settings.domain.model.ThemeSettings

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val context: Context
) : ViewModel() {

    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as PlaylistApplication)
                SettingsViewModel(
                    Creator.provideSharingInteractor(app),
                    Creator.provideSettingsInteractor(app),
                    app)

            }
        }
    }

    private val settingsLiveData = MutableLiveData<ThemeSettings>()
    fun observeSettingsLiveData(): LiveData<ThemeSettings> = settingsLiveData

    fun manageTheme(isChecked: Boolean) {
        val theme = if (isChecked) ThemeSettings.NIGHT_MODE else ThemeSettings.DAY_MODE
        settingsInteractor.updateThemeSetting(theme)
        (context as PlaylistApplication).switchTheme(isChecked)
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