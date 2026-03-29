package com.bignerdranch.android.playlistmaker.util

import android.content.Context
import android.util.TypedValue
import com.bignerdranch.android.playlistmaker.media_library.data.db.entity.PlaylistEntity
import com.bignerdranch.android.playlistmaker.media_library.data.db.entity.TrackEntity
import com.bignerdranch.android.playlistmaker.media_library.data.db.entity.TrackInPlaylistEntity
import com.bignerdranch.android.playlistmaker.media_library.data.dto.PlaylistDto
import com.bignerdranch.android.playlistmaker.media_library.domain.models.Playlist
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.String

object Converter {
    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    fun longToMMSS(value: Long): String {
        return  SimpleDateFormat("mm:ss", Locale.getDefault()).format(value)
    }

    fun dateToYear(value: String?): String? {
        return value?.take(4)
    }

    fun getCoverArtwork(artworkUrl100: String?): String? {
        return artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
    }


    fun trackToEntity(track: Track): TrackEntity {
        return TrackEntity(
             track.id,
            track.artworkUrl,
            track.trackName,
            track.artistName,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.trackDuration,
            track.previewUrl)
    }

    fun trackToTrackInPlaylistEntity(track: Track): TrackInPlaylistEntity {
        return TrackInPlaylistEntity(
            track.id,
            track.artworkUrl,
            track.trackName,
            track.artistName,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.trackDuration,
            track.previewUrl)
    }

    fun entityToTrack(track: TrackEntity): Track {
        return Track(
            trackName = track.trackName,
            artistName = track.artistName,
            trackDuration =  track.trackDuration,
            artworkUrl = track.artworkUrl,
            id = track.trackId,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }

    fun trackInPlaylistEntityToTrack(track: TrackInPlaylistEntity): Track {
        return Track(
            trackName = track.trackName,
            artistName = track.artistName,
            trackDuration =  track.trackDuration,
            artworkUrl = track.artworkUrl,
            id = track.trackId,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }

    fun playlistDtoToEntity(playlist: PlaylistDto): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.playlistId,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            imagePath = playlist.imagePath,
            tracksIds = playlist.tracksIds,
        )
    }

    fun entityToPlaylistDto(playlistEntity: PlaylistEntity): PlaylistDto {
        return PlaylistDto(
            playlistId = playlistEntity.playlistId,
            playlistName = playlistEntity.playlistName,
            playlistDescription = playlistEntity.playlistDescription,
            imagePath = playlistEntity.imagePath,
            tracksIds = playlistEntity.tracksIds
        )
    }

    fun playlistDtoToPlaylist(playlistDto: PlaylistDto): Playlist {
        return Playlist(
            playlistId = playlistDto.playlistId,
            playlistName = playlistDto.playlistName,
            playlistDescription = playlistDto.playlistDescription,
            imagePath = playlistDto.imagePath,
            tracksIds = playlistDto.tracksIds
        )
    }

}