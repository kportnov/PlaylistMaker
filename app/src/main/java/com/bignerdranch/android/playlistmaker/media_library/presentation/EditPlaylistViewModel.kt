package com.bignerdranch.android.playlistmaker.media_library.presentation

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.playlistmaker.media_library.domain.db.PlaylistsInteractor
import com.bignerdranch.android.playlistmaker.media_library.ui.models.CreatePlaylistState
import kotlinx.coroutines.launch
import java.io.File

class EditPlaylistViewModel(
    playlistInteractor: PlaylistsInteractor
) : CreatePlaylistViewModel(playlistInteractor) {

    fun processState(playlistId: Int) {
        viewModelScope.launch {
            val playlist = playlistsInteractor.getPlaylistById(playlistId)

            val imageUri = playlist.imagePath
                ?.takeIf { it.isNotEmpty() }
                ?.let { Uri.fromFile(File(it)) }

            renderState(CreatePlaylistState(
                playlist.playlistName,
                playlist.playlistDescription,
                imageUri
            ))
        }
    }

    fun editPlaylist(playlistId: Int) {
        viewModelScope.launch {
            val playlist = playlistsInteractor.getPlaylistById(playlistId)

            val imagePath = _state.value?.coverUri?.let { uri ->
                playlistsInteractor.saveImage(uri)
            }

            val updatedPlaylist = playlist.copy(
                playlistId = playlistId,
                playlistName = _state.value?.title ?: "",
                playlistDescription = _state.value?.description,
                imagePath = imagePath,
                tracksIds =  playlist.tracksIds
            )
            playlistsInteractor.updatePlaylist(updatedPlaylist)
        }
    }

    fun renderState(state: CreatePlaylistState) {
        _state.postValue(state)
    }
}
