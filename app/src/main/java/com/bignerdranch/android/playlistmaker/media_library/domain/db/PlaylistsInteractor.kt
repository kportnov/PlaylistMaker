package com.bignerdranch.android.playlistmaker.media_library.domain.db

import android.net.Uri
import com.bignerdranch.android.playlistmaker.media_library.domain.models.Playlist
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun createPlaylist(playlist: Playlist)
    suspend fun deletePlaylistById(playlistId: Int)
    suspend fun getPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylistById(playlistId: Int): Playlist
    suspend fun getTracksByIds(ids: List<String>): Flow<List<Track>>
    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)
    suspend fun deleteTrackFromPlaylist(trackID: String, playlistId: Int)
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun saveImage(uri: Uri): String
}