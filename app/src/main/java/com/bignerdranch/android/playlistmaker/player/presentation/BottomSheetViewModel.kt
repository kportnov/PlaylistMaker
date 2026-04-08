package com.bignerdranch.android.playlistmaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.playlistmaker.media_library.domain.db.PlaylistsInteractor
import com.bignerdranch.android.playlistmaker.media_library.domain.models.Playlist
import com.bignerdranch.android.playlistmaker.player.ui.model.AddTrackState
import com.bignerdranch.android.playlistmaker.player.ui.model.PlaylistsUiState
import com.bignerdranch.android.playlistmaker.search.domain.api.TracksHistoryInteractor
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.collections.firstOrNull

class BottomSheetViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val tracksHistoryInteractor: TracksHistoryInteractor
) : ViewModel() {

    private val playlistLiveData = MutableLiveData<PlaylistsUiState>()
    fun observePlaylistLiveData(): LiveData<PlaylistsUiState> = playlistLiveData


    fun addTrackToPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            val history = tracksHistoryInteractor.getHistory().firstOrNull()
            val track = history?.firstOrNull()
            if (track != null) {
                if (playlist.tracksIds.contains(track.id)) {
                    playlistLiveData.postValue(
                        PlaylistsUiState.AddTrackStatus(AddTrackState.AlreadyExists(playlist))
                    )
                } else {
                    playlistsInteractor.addTrackToPlaylist(track, playlist)
                    playlistLiveData.postValue(
                        PlaylistsUiState.AddTrackStatus(AddTrackState.Added(playlist))
                    )
                    getPlaylists()
                }
            }
        }
    }

    fun getPlaylists() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect {
                playlistLiveData.postValue(PlaylistsUiState.Content(it))
            }
        }
    }
}