package com.bignerdranch.android.playlistmaker.di

import com.bignerdranch.android.playlistmaker.media_library.presentation.FavoritesViewModel
import com.bignerdranch.android.playlistmaker.media_library.presentation.PlaylistsViewModel
import com.bignerdranch.android.playlistmaker.media_library.presentation.CreatePlaylistViewModel
import com.bignerdranch.android.playlistmaker.media_library.presentation.CurrentPlaylistViewModel
import com.bignerdranch.android.playlistmaker.media_library.presentation.EditPlaylistViewModel
import com.bignerdranch.android.playlistmaker.player.presentation.BottomSheetViewModel
import com.bignerdranch.android.playlistmaker.player.presentation.PlayerViewModel
import com.bignerdranch.android.playlistmaker.search.ui.SearchViewModel
import com.bignerdranch.android.playlistmaker.settings.ui.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel {
        PlayerViewModel(get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        FavoritesViewModel(get(), get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel {
        CreatePlaylistViewModel(get())
    }

    viewModel {
        BottomSheetViewModel(get(), get())
    }

    viewModel {
        CurrentPlaylistViewModel(get(), get(), get(), get())
    }

    viewModel {
        EditPlaylistViewModel(get())
    }
}