package com.bignerdranch.android.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    fun addToHistoryList(track: Track) {
        val trackHistory = readSharedPreferences().toMutableList()
        // Почему обязательно по trackId, почему нельзя просто сравнивать tracks? Ведь DataClass же
        if (trackHistory.map { it.trackId }.contains(track.trackId)) {
            trackHistory.remove(trackHistory.find { it.trackId == track.trackId })
            trackHistory.add(0, track)
        } else {
            trackHistory.add(0, track)
        }
        if (trackHistory.size > 10) {
            trackHistory.removeAt(trackHistory.size - 1)
        }
        writeSharedPreferences(trackHistory)
    }

    fun clearHistoryList() {
        writeSharedPreferences(mutableListOf())
    }

    fun readSharedPreferences(): MutableList<Track> {
        val json = sharedPreferences.getString(SEARCH_HISTORY_KEY, null) ?: return mutableListOf()
        return Gson().fromJson(json, Array<Track>::class.java).toMutableList()
    }

    private fun writeSharedPreferences(trackHistory: MutableList<Track>) {
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_KEY, Gson().toJson(trackHistory.toTypedArray()))
            .apply()
    }
}