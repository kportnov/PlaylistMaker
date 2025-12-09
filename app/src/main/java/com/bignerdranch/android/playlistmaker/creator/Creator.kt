package com.bignerdranch.android.playlistmaker.creator

import android.app.Application
import android.content.Context
import com.bignerdranch.android.playlistmaker.search.data.TracksHistoryRepositoryImpl
import com.bignerdranch.android.playlistmaker.search.data.TracksRepositoryImpl
import com.bignerdranch.android.playlistmaker.search.data.dto.TrackHistoryDto
import com.bignerdranch.android.playlistmaker.search.data.network.RetrofitNetworkClient
import com.bignerdranch.android.playlistmaker.search.data.storage.PrefsStorageClient
import com.bignerdranch.android.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.bignerdranch.android.playlistmaker.search.domain.api.TracksHistoryRepository
import com.bignerdranch.android.playlistmaker.search.domain.api.TracksInteractor
import com.bignerdranch.android.playlistmaker.search.domain.api.TracksRepository
import com.bignerdranch.android.playlistmaker.search.domain.impl.TracksHistoryInteractorImpl
import com.bignerdranch.android.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.bignerdranch.android.playlistmaker.settings.data.ExternalNavigator
import com.bignerdranch.android.playlistmaker.settings.data.SettingsRepositoryImpl
import com.bignerdranch.android.playlistmaker.settings.domain.api.SettingsInteractor
import com.bignerdranch.android.playlistmaker.settings.domain.api.SettingsRepository
import com.bignerdranch.android.playlistmaker.settings.domain.api.SharingInteractor
import com.bignerdranch.android.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.bignerdranch.android.playlistmaker.settings.domain.impl.SharingInteractorImpl
import com.bignerdranch.android.playlistmaker.settings.domain.model.ThemeSettings
import com.bignerdranch.android.playlistmaker.util.SEARCH_HISTORY_KEY
import com.bignerdranch.android.playlistmaker.util.THEME_KEY

import com.google.gson.reflect.TypeToken

object Creator {
    private lateinit var application: Application

    fun initApplication(application: Application) {
        this.application = application
    }
    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }

    private fun getTracksHistoryRepository(context: Context): TracksHistoryRepository {
        return TracksHistoryRepositoryImpl(
            PrefsStorageClient(
                context,
                SEARCH_HISTORY_KEY,
            object : TypeToken<ArrayList<TrackHistoryDto>>() {}.type
            )
        )
    }

    fun provideTracksHistoryInteractor(context: Context): TracksHistoryInteractor {
        return TracksHistoryInteractorImpl(getTracksHistoryRepository(context))
    }

    private fun getSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(
            PrefsStorageClient(
                context,
                THEME_KEY,
                object : TypeToken<ThemeSettings>() {}.type
            )
        )
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(context))
    }

    private fun getExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigator(context)
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(getExternalNavigator(context))
    }
}