package com.bignerdranch.android.playlistmaker.media_library.data.db

import com.bignerdranch.android.playlistmaker.media_library.data.analytics.Analytics
import com.bignerdranch.android.playlistmaker.media_library.data.db.entity.TrackEntity
import com.bignerdranch.android.playlistmaker.media_library.domain.db.FavoritesRepository
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.util.Converter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val analytics: Analytics
): FavoritesRepository {
    override suspend fun addToFavorites(track: Track) {
        appDatabase.trackDao().insertTrack(Converter.trackToEntity(track))
        analytics.logAddToFavoritesEvent(track.trackName)
    }

    override suspend fun deleteFromFavorites(track: Track) {
        appDatabase.trackDao().deleteTrack(Converter.trackToEntity(track))
        analytics.logDeleteFromFavorites(track.trackName)
    }

    override suspend fun getFavorites(): Flow<List<Track>> {
        return appDatabase.trackDao().getTracks().map {
            trackEntities -> convertFromTrackEntity(trackEntities)
        }
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks
            .map { track -> Converter.entityToTrack(track) }
            .reversed()
    }
}