package com.bignerdranch.android.playlistmaker.domain.api

import com.bignerdranch.android.playlistmaker.domain.models.Track

interface TracksHistoryInteractor {
    fun loadHistory(consumer: TracksConsumer)
    fun addToHistory(track: Track)
    fun clearHistory()
    interface TracksConsumer {
        fun consume(tracks: List<Track>)
    }
}