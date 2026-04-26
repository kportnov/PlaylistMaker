package com.bignerdranch.android.playlistmaker.media_library.data.analytics

interface Analytics {
    fun logAddToFavoritesEvent(songName: String)
    fun logDeleteFromFavorites(songName: String)
}