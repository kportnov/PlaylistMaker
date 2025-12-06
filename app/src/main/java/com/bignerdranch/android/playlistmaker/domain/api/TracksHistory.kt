package com.bignerdranch.android.playlistmaker.domain.api

import com.bignerdranch.android.playlistmaker.domain.models.Track

interface TracksHistory {

    fun saveHistory(tracksHistory: List<Track>)
    fun loadHistoryList(): List<Track>
    fun addToHistoryList(track: Track)
    fun clearHistoryList()

}