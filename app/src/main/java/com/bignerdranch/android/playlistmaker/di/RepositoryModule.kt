package com.bignerdranch.android.playlistmaker.di

import com.bignerdranch.android.playlistmaker.search.data.TracksHistoryRepositoryImpl
import com.bignerdranch.android.playlistmaker.search.data.TracksRepositoryImpl
import com.bignerdranch.android.playlistmaker.search.domain.api.TracksHistoryRepository
import com.bignerdranch.android.playlistmaker.search.domain.api.TracksRepository
import com.bignerdranch.android.playlistmaker.settings.data.SettingsRepositoryImpl
import com.bignerdranch.android.playlistmaker.settings.domain.api.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {

    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    single<TracksHistoryRepository> {
        TracksHistoryRepositoryImpl(get(named("STORAGE_HISTORY")))
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get(named("STORAGE_THEME")), androidContext())
    }
}