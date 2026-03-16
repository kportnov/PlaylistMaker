package com.bignerdranch.android.playlistmaker.media_library.presentation

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.playlistmaker.media_library.domain.db.PlaylistsInteractor
import com.bignerdranch.android.playlistmaker.media_library.domain.models.Playlist
import com.bignerdranch.android.playlistmaker.media_library.ui.models.CreatePlaylistState
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
    ) : ViewModel() {

    private val _state = MutableLiveData(CreatePlaylistState())
    val observeCreatePlaylistState: LiveData<CreatePlaylistState> = _state

    fun updateTitle(title: String) {
        _state.value = _state.value?.copy(title = title)
    }

    fun updateDescription(description: String) {
        _state.value = _state.value?.copy(description = description)
    }

    fun updateCover(uri: Uri) {
        _state.value = _state.value?.copy(coverUri = uri)
    }

    fun createPlaylist() {
        viewModelScope.launch {

            val imagePath = _state.value?.coverUri?.let { uri ->
                playlistsInteractor.saveImage(uri)
            }

            val playlist = Playlist(
                playlistName = _state.value?.title ?: "",
                playlistDescription = _state.value?.description,
                tracksIds = emptyList(),
                imagePath = imagePath
            )

            playlistsInteractor.createPlaylist(playlist)
        }
    }
}