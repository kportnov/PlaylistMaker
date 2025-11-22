package com.bignerdranch.android.playlistmaker.data

import android.content.Context
import com.bignerdranch.android.playlistmaker.domain.models.Track
import com.bignerdranch.android.playlistmaker.presentation.search.SEARCH_HISTORY_KEY
import com.bignerdranch.android.playlistmaker.presentation.search.SHARED_PREFERENCES_SEARCH
import com.google.gson.Gson
import kotlin.collections.remove


private const val MAXIMUM_SEARCH_HISTORY_ITEMS = 10

class TrackHistoryManager(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_SEARCH, Context.MODE_PRIVATE)
    private val gson = Gson()

    private fun saveHistory(tracksHistory: List<Track>) {
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_KEY, gson.toJson(tracksHistory.toTypedArray()))
            .apply()
    }

    fun loadHistoryList(): List<Track> {
        val json = sharedPreferences.getString(SEARCH_HISTORY_KEY, null)
        return json?.let { gson.fromJson(json, Array<Track>::class.java).toList() } ?: emptyList()
    }

    fun clearHistoryList() {
        saveHistory(emptyList())
    }

    fun addToHistoryList(track: Track) {
        val trackHistory = loadHistoryList().toMutableList()
        if (trackHistory.map { it.trackId }.contains(track.trackId)) {
            trackHistory.remove(trackHistory.find { it.trackId == track.trackId })
            trackHistory.add(0, track)
        } else {
            trackHistory.add(0, track)
        }
        if (trackHistory.size > MAXIMUM_SEARCH_HISTORY_ITEMS) {
            trackHistory.removeAt(trackHistory.size - 1)
        }
        saveHistory(trackHistory)
    }

}