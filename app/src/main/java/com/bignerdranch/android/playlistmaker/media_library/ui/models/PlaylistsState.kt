package com.bignerdranch.android.playlistmaker.media_library.ui.models

import com.bignerdranch.android.playlistmaker.media_library.domain.models.Playlist

sealed interface PlaylistsState {

    data class Content(
        val playlists: List<Playlist>
    ): PlaylistsState

    object Empty: PlaylistsState

}