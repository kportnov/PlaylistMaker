package com.bignerdranch.android.playlistmaker.domain.impl

import com.bignerdranch.android.playlistmaker.data.TrackHistoryManager
import com.bignerdranch.android.playlistmaker.domain.api.TracksHistoryInteractor
import com.bignerdranch.android.playlistmaker.domain.models.Track

class TracksHistoryInteractorImpl(private val trackHistoryManager: TrackHistoryManager) : TracksHistoryInteractor {

    override fun loadHistory(consumer: TracksHistoryInteractor.TracksConsumer) {
        consumer.consume(trackHistoryManager.loadHistoryList())
    }

    override fun addToHistory(track: Track) {
        trackHistoryManager.addToHistoryList(track)
    }

    override fun clearHistory() {
        trackHistoryManager.clearHistoryList()
    }

}