package com.bignerdranch.android.playlistmaker.media_library.domain.impl

import com.bignerdranch.android.playlistmaker.media_library.domain.db.FavoritesInteractor
import com.bignerdranch.android.playlistmaker.media_library.domain.db.FavoritesRepository
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(
    val favoritesRepository: FavoritesRepository
): FavoritesInteractor {

    override suspend fun addToFavorites(track: Track) {
        favoritesRepository.addToFavorites(track)
    }

    override suspend fun deleteFromFavorites(track: Track) {
        favoritesRepository.deleteFromFavorites(track)
    }

    override suspend fun getFavorites(): Flow<List<Track>> {
        return favoritesRepository.getFavorites()
    }
}