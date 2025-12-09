package com.bignerdranch.android.playlistmaker.search.domain.api

import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.util.Resource

interface TracksHistoryRepository {
    fun addToHistory(track: Track)
    fun saveHistory(tracksHistory: List<Track>)
    fun getHistory(): Resource<List<Track>>
    fun clearHistory()
}