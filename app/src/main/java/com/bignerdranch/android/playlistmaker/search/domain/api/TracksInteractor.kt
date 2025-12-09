package com.bignerdranch.android.playlistmaker.search.domain.api

import com.bignerdranch.android.playlistmaker.search.domain.models.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        //пока сообщение об причины ошибки не нужно
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }
}