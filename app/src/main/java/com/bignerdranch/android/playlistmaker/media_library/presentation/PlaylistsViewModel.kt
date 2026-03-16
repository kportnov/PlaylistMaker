package com.bignerdranch.android.playlistmaker.media_library.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.playlistmaker.media_library.domain.db.PlaylistsInteractor
import com.bignerdranch.android.playlistmaker.media_library.domain.models.Playlist
import com.bignerdranch.android.playlistmaker.media_library.ui.models.PlaylistsState
import kotlinx.coroutines.launch

class PlaylistsViewModel(
   val playlistsInteractor: PlaylistsInteractor
): ViewModel() {
    private val playlistsLiveData = MutableLiveData<PlaylistsState>()
    fun observeLiveData(): LiveData<PlaylistsState> = playlistsLiveData


    fun getPlaylists() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect {
                processPlaylists(it)
            }
        }
    }

    private fun processPlaylists(playlists: List<Playlist>?) {
        if (playlists.isNullOrEmpty()) {
            renderState(PlaylistsState.Empty)
        } else {
            renderState(PlaylistsState.Content(playlists))
        }
    }

    private fun renderState(state: PlaylistsState) {
        playlistsLiveData.postValue(state)
    }
}