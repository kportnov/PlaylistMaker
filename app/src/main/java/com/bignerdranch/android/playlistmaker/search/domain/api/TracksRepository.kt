package com.bignerdranch.android.playlistmaker.search.domain.api

import com.bignerdranch.android.playlistmaker.search.domain.models.Track

interface TracksRepository {
    fun searchTracks(expression: String): Result<List<Track>>
}

