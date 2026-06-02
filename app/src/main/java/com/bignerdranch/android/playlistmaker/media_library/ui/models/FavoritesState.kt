package com.bignerdranch.android.playlistmaker.media_library.ui.models

import androidx.compose.runtime.Immutable
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import kotlinx.collections.immutable.ImmutableList

@Immutable
sealed interface FavoritesState {
    @Immutable
    data class Content(
        val tracks: ImmutableList<Track>
    ): FavoritesState
    @Immutable
    data class Empty(
        val messageId: Int
    ): FavoritesState
}