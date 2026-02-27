package com.bignerdranch.android.playlistmaker.di

import com.bignerdranch.android.playlistmaker.media_library.presentation.FavoritesViewModel
import com.bignerdranch.android.playlistmaker.media_library.presentation.PlaylistsViewModel
import com.bignerdranch.android.playlistmaker.player.ui.PlayerViewModel
import com.bignerdranch.android.playlistmaker.search.ui.SearchViewModel
import com.bignerdranch.android.playlistmaker.settings.ui.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel {
        PlayerViewModel(get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        FavoritesViewModel(get(), get())
    }

    viewModel {
        PlaylistsViewModel()
    }
}