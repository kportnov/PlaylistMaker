package com.bignerdranch.android.playlistmaker.search.domain.api

import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(expression: String): Flow<Result<List<Track>>>
}

