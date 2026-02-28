package com.bignerdranch.android.playlistmaker.search.domain.api

import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksHistoryInteractor {
    fun getHistory(): Flow<List<Track>>
    fun addToHistory(track: Track)
    fun clearHistory()
}