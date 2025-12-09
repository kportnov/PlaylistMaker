package com.bignerdranch.android.playlistmaker.search.domain.api

import com.bignerdranch.android.playlistmaker.search.domain.models.Track

interface TracksHistoryRepository {
    fun addToHistory(track: Track)
    fun saveHistory(tracksHistory: List<Track>)
    fun getHistory(): Result<List<Track>>
    fun clearHistory()
}