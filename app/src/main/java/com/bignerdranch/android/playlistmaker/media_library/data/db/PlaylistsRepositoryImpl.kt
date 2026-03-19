package com.bignerdranch.android.playlistmaker.media_library.data.db

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.bignerdranch.android.playlistmaker.media_library.data.db.entity.PlaylistEntity
import com.bignerdranch.android.playlistmaker.media_library.data.dto.PlaylistDto
import com.bignerdranch.android.playlistmaker.media_library.domain.db.PlaylistsRepository
import com.bignerdranch.android.playlistmaker.media_library.domain.models.Playlist
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.util.Converter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.FileOutputStream
import kotlin.collections.map
import kotlin.collections.reversed

class PlaylistsRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val context: Context
): PlaylistsRepository {
    override suspend fun createPlaylist(playlist: Playlist) {
        val playlistDto = PlaylistDto(
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            imagePath = playlist.imagePath,
            tracksIds = emptyList()
        )
        appDatabase.playlistDao().insertPlaylist(Converter.playlistDtoToEntity(playlistDto))
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val playlistDto = PlaylistDto(
            playlistId = playlist.playlistId,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            imagePath = playlist.imagePath,
            tracksIds = playlist.tracksIds,
            tracksNumber = playlist.tracksNumber
        )
        appDatabase.playlistDao().deletePlaylist(Converter.playlistDtoToEntity(playlistDto))
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getPlaylists().map {
            playlistEntities -> convertFromPlaylistEntity(playlistEntities)
        }
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {

        val updatedTracksIds = playlist.tracksIds.toMutableList()
        updatedTracksIds.add(track.id)

        val playlistDto = PlaylistDto(
            playlistId = playlist.playlistId,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            imagePath = playlist.imagePath,
            tracksIds = updatedTracksIds,
            tracksNumber = playlist.tracksNumber + 1
        )
        appDatabase.playlistDao().updatePlaylist(Converter.playlistDtoToEntity(playlistDto))
        appDatabase.trackInPlaylistDao().insertTrack(Converter.trackToTrackInPlaylistEntity(track))
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists
            .map { playlistEntity -> Converter.entityToPlaylistDto(playlistEntity) }
            .map { Playlist(
                playlistId = it.playlistId,
                playlistName = it.playlistName,
                playlistDescription = it.playlistDescription,
                imagePath = it.imagePath,
                tracksIds = it.tracksIds,
                tracksNumber = it.tracksNumber
            ) }
            .reversed()
    }

    override suspend fun saveImage(uri: Uri): String {
        val filePath = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "myalbum"
        )
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val fileName = "cover_${System.currentTimeMillis()}.jpg"
        val file = File(filePath, fileName)

        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        return file.absolutePath
    }
}