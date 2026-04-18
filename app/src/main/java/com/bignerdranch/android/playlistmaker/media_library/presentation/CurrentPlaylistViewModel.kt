package com.bignerdranch.android.playlistmaker.media_library.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.playlistmaker.media_library.domain.db.PlaylistsInteractor
import com.bignerdranch.android.playlistmaker.media_library.presentation.formatter.PlaylistShareFormatter
import com.bignerdranch.android.playlistmaker.media_library.presentation.navigator.ExternalNavigatorShare
import com.bignerdranch.android.playlistmaker.media_library.ui.models.PlaylistCurrentState
import com.bignerdranch.android.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.util.Converter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CurrentPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val tracksHistoryInteractor: TracksHistoryInteractor,
    private val externalNavigatorShare: ExternalNavigatorShare,
    private val playlistShareFormatter: PlaylistShareFormatter
) : ViewModel() {

    private val currentPlaylistLiveData = MutableLiveData<PlaylistCurrentState>()
    fun observeLiveData(): LiveData<PlaylistCurrentState> = currentPlaylistLiveData

    fun processPlaylist(id: Int) {
        viewModelScope.launch {
            val playlist = playlistsInteractor.getPlaylistById(id)
            val tracksIds = playlist.tracksIds
            playlistsInteractor.getTracksByIds(tracksIds).collect {

                renderState(PlaylistCurrentState(
                    id = playlist.playlistId,
                    title = playlist.playlistName,
                    description = playlist.playlistDescription,
                    imagePath = playlist.imagePath,
                    duration = getDuration(it),
                    tracks = it
                ))
            }
        }
    }

    fun deleteTrackFromPlaylist(trackId: String, playlistId: Int) {
        viewModelScope.launch {
            playlistsInteractor.deleteTrackFromPlaylist(trackId, playlistId)
            processPlaylist(playlistId)
        }
    }

    private fun getDuration(tracks: List<Track>): String {
        val duration = tracks
            .mapNotNull { track -> track.trackDuration?.toLongOrNull() }
            .sum()

        return Converter.longToMMSS(duration)
    }

    private fun renderState(state: PlaylistCurrentState) {
        currentPlaylistLiveData.postValue(state)
    }

    fun addToHistory(track: Track) {
        tracksHistoryInteractor.addToHistory(track)
    }

    fun shareThePlaylist(playlistId: Int) {
        viewModelScope.launch {
            val playlist = playlistsInteractor.getPlaylistById(playlistId)

            val tracks = playlistsInteractor
                .getTracksByIds(playlist.tracksIds)
                .first()

            val message = playlistShareFormatter.format(playlist, tracks)
            externalNavigatorShare.sharePlaylist(message)
        }
    }

    suspend fun deletePlaylistById(playlistId: Int) {
        playlistsInteractor.deletePlaylistById(playlistId)

    }
}