package com.bignerdranch.android.playlistmaker.di

import com.bignerdranch.android.playlistmaker.player.ui.PlayerViewModel
import com.bignerdranch.android.playlistmaker.search.ui.SearchViewModel
import com.bignerdranch.android.playlistmaker.settings.ui.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get(), get(), androidContext())
    }

    viewModel {
        PlayerViewModel(get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }
}