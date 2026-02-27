package com.bignerdranch.android.playlistmaker.search.domain.impl

import com.bignerdranch.android.playlistmaker.search.domain.api.TracksHistoryRepository
import com.bignerdranch.android.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class TracksHistoryInteractorImpl(private val repository: TracksHistoryRepository) : TracksHistoryInteractor {
    override fun getHistory(): Flow<List<Track>> {
        return repository.getHistory()
    }

    override fun addToHistory(track: Track) {
        repository.addToHistory(track)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }

}