package com.bignerdranch.android.playlistmaker.search.domain.impl

import com.bignerdranch.android.playlistmaker.search.domain.api.TracksHistoryRepository
import com.bignerdranch.android.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.bignerdranch.android.playlistmaker.search.domain.models.Track

class TracksHistoryInteractorImpl(private val repository: TracksHistoryRepository) : TracksHistoryInteractor {

    override fun getHistory(consumer: TracksHistoryInteractor.TracksConsumer) {
        consumer.consume(repository.getHistory().getOrNull())
    }

    override fun addToHistory(track: Track) {
        repository.addToHistory(track)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }

}