package com.bignerdranch.android.playlistmaker.media_library.ui.models

import com.bignerdranch.android.playlistmaker.media_library.domain.models.Playlist

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
sealed interface PlaylistsState {

    @Immutable
    data class Content(
        val playlists: ImmutableList<Playlist>
    ) : PlaylistsState

    @Immutable
    data class Empty(
        val messageId: Int
    ) : PlaylistsState
}