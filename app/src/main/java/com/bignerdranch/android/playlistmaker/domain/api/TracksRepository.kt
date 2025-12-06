package com.bignerdranch.android.playlistmaker.domain.api

import com.bignerdranch.android.playlistmaker.domain.models.Track

interface TracksRepository {
    fun searchTracks(expression: String): List<Track>
}

