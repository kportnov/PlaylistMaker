package com.bignerdranch.android.playlistmaker.media_library.domain.impl

import android.net.Uri
import com.bignerdranch.android.playlistmaker.media_library.domain.db.PlaylistsInteractor
import com.bignerdranch.android.playlistmaker.media_library.domain.db.PlaylistsRepository
import com.bignerdranch.android.playlistmaker.media_library.domain.models.Playlist
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(
    private val playlistsRepository: PlaylistsRepository
): PlaylistsInteractor {
    override suspend fun createPlaylist(playlist: Playlist) {
        playlistsRepository.createPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistsRepository.deletePlaylist(playlist)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return playlistsRepository.getPlaylists()
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        playlistsRepository.addTrackToPlaylist(track, playlist)
    }

    override suspend fun saveImage(uri: Uri): String {
        return playlistsRepository.saveImage(uri)
    }
}