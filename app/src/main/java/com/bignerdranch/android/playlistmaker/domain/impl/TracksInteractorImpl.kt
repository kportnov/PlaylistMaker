package com.bignerdranch.android.playlistmaker.domain.impl

import com.bignerdranch.android.playlistmaker.domain.api.TracksInteractor
import com.bignerdranch.android.playlistmaker.domain.api.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(
        expression: String,
        consumer: TracksInteractor.TracksConsumer
    ) {
        executor.execute { consumer.consume(repository.searchTracks(expression)) }
    }
}