package com.bignerdranch.android.playlistmaker.search.ui.models

import com.bignerdranch.android.playlistmaker.search.domain.models.Track

sealed interface SearchState {

    object Loading : SearchState {
        override fun toString(): String {
            return "Loading()"
        }
    }

    data class Content(
        val tracks: List<Track>
    ) : SearchState {
        override fun toString(): String {
            return "Content"
        }
    }

    data class History(
        val tracks: List<Track>
    ) : SearchState {
        override fun toString(): String {
            return "History"
        }
    }

    data class Error(
        val errorMessage: String
    ) : SearchState {
        override fun toString(): String {
            return "Error"
        }
    }

    data class Empty(
        val message: String
    ) : SearchState {
        override fun toString(): String {
            return "Empty"
        }
    }
}