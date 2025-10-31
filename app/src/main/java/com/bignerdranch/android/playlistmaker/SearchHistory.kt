package com.bignerdranch.android.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

private const val MAXIMUM_SEARCH_HISTORY_ITEMS = 10

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    private val gson = Gson()

    fun addToHistoryList(track: Track) {
        val trackHistory = readSharedPreferences().toMutableList()
        // Почему обязательно по trackId, почему нельзя просто сравнивать tracks? Ведь DataClass же
        if (trackHistory.map { it.trackId }.contains(track.trackId)) {
            trackHistory.remove(trackHistory.find { it.trackId == track.trackId })
            trackHistory.add(0, track)
        } else {
            trackHistory.add(0, track)
        }
        if (trackHistory.size > MAXIMUM_SEARCH_HISTORY_ITEMS) {
            trackHistory.removeAt(trackHistory.size - 1)
        }
        writeSharedPreferences(trackHistory)
    }

    fun clearHistoryList() {
        writeSharedPreferences(listOf())
    }

    fun readSharedPreferences(): List<Track> {
        val json = sharedPreferences.getString(SEARCH_HISTORY_KEY, null) ?: return listOf()
        return gson.fromJson(json, Array<Track>::class.java).toList()
    }

    private fun writeSharedPreferences(trackHistory: List<Track>) {
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_KEY, gson.toJson(trackHistory.toTypedArray()))
            .apply()
    }
}