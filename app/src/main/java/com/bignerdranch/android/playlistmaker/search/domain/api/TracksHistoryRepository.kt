package com.bignerdranch.android.playlistmaker.search.domain.api

import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksHistoryRepository {
    fun addToHistory(track: Track)
    fun saveHistory(tracksHistory: List<Track>)
    fun getHistory(): Flow<List<Track>>
    fun clearHistory()
}