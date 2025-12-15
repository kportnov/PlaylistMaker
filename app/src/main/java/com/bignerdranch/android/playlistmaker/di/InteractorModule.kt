package com.bignerdranch.android.playlistmaker.di

import com.bignerdranch.android.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.bignerdranch.android.playlistmaker.search.domain.api.TracksInteractor
import com.bignerdranch.android.playlistmaker.search.domain.impl.TracksHistoryInteractorImpl
import com.bignerdranch.android.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.bignerdranch.android.playlistmaker.settings.domain.api.SettingsInteractor
import com.bignerdranch.android.playlistmaker.settings.domain.api.SharingInteractor
import com.bignerdranch.android.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.bignerdranch.android.playlistmaker.settings.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    single<TracksInteractor> {
        TracksInteractorImpl(get(), get())
    }

    single<TracksHistoryInteractor> {
        TracksHistoryInteractorImpl(get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(get())
    }
}