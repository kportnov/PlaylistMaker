package com.bignerdranch.android.playlistmaker.media_library.ui.models

import com.bignerdranch.android.playlistmaker.search.domain.models.Track

sealed interface FavoritesState {

    data class Content(
        val tracks: List<Track>
    ): FavoritesState

    data class Empty(
        val messageId: Int
    ): FavoritesState
}