package com.bignerdranch.android.playlistmaker.media_library.data.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent

class FirebaseAnalyticsImpl(private val firebaseAnalytics: FirebaseAnalytics): Analytics {

    override fun logAddToFavoritesEvent(songName: String) {
        firebaseAnalytics.logEvent("add_to_favorites") {
            param("name", songName)
        }
    }

    override fun logDeleteFromFavorites(songName: String) {
        firebaseAnalytics.logEvent("delete_from_favorites") {
            param("name", songName)
        }
    }
}