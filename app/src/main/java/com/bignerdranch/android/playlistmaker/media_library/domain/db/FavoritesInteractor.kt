package com.bignerdranch.android.playlistmaker.media_library.domain.db

import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {

    suspend fun addToFavorites(track: Track)
    suspend fun deleteFromFavorites(track: Track)
    suspend fun getFavorites(): Flow<List<Track>>
}