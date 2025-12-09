package com.bignerdranch.android.playlistmaker.settings.domain.impl

import com.bignerdranch.android.playlistmaker.settings.data.ExternalNavigator
import com.bignerdranch.android.playlistmaker.settings.domain.api.SharingInteractor

class SharingInteractorImpl(private val externalNavigator: ExternalNavigator) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareTheApp()
    }

    override fun openTerms() {
        externalNavigator.openTerms()
    }

    override fun openSupport() {
        externalNavigator.sendToSupport()
    }
}