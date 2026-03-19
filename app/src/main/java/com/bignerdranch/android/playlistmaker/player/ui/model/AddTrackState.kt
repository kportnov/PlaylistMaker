package com.bignerdranch.android.playlistmaker.player.ui.model

import com.bignerdranch.android.playlistmaker.media_library.domain.models.Playlist


sealed interface PlaylistsUiState {
    data class Content(
        val playlists: List<Playlist>
    ) : PlaylistsUiState

    data class AddTrackStatus(
        val state: AddTrackState
    ) : PlaylistsUiState


}

sealed interface AddTrackState {
    data class AlreadyExists(val playlist: Playlist) : AddTrackState
    data class Added(val playlist: Playlist) : AddTrackState
}
