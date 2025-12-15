package com.bignerdranch.android.playlistmaker.search.domain.impl

import com.bignerdranch.android.playlistmaker.search.domain.api.TracksInteractor
import com.bignerdranch.android.playlistmaker.search.domain.api.TracksRepository
import java.util.concurrent.ExecutorService

class TracksInteractorImpl(
    private val repository: TracksRepository,
    private val executor: ExecutorService
    ) : TracksInteractor {

    override fun searchTracks(
        expression: String,
        consumer: TracksInteractor.TracksConsumer
    ) {
        executor.execute {
            val resource = repository.searchTracks(expression)
            if (resource.isSuccess) {
                consumer.consume(resource.getOrNull(), null)
            }
            if (resource.isFailure) {
                consumer.consume(null, resource.exceptionOrNull()?.message)
            }
        }
    }
}