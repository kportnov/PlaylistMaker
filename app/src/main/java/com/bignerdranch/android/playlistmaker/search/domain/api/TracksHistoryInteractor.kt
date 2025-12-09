package com.bignerdranch.android.playlistmaker.search.domain.api

import com.bignerdranch.android.playlistmaker.search.domain.models.Track

interface TracksHistoryInteractor {
    fun getHistory(consumer: TracksConsumer)
    fun addToHistory(track: Track)
    fun clearHistory()
    interface TracksConsumer {
        fun consume(tracks: List<Track>?)
    }
}