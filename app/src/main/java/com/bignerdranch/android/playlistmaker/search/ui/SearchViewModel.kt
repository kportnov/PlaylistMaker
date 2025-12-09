package com.bignerdranch.android.playlistmaker.search.ui

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.bignerdranch.android.playlistmaker.PlaylistApplication
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.creator.Creator
import com.bignerdranch.android.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.bignerdranch.android.playlistmaker.search.domain.api.TracksInteractor
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.search.ui.models.SearchState

class SearchViewModel(private val context: Context): ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L

        private val SEARCH_REQUEST_TOKEN = Any()

        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as PlaylistApplication)
                SearchViewModel(app)
            }
        }

    }

    private val tracksInteractor = Creator.provideTracksInteractor(context)
    private val tracksHistoryInteractor = Creator.provideTracksHistoryInteractor(context)
    private var latestSearchText: String? = null
    private val handler = Handler(Looper.getMainLooper())

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData


    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }
        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    fun clearHistory() {
        tracksHistoryInteractor.clearHistory()
        renderState(SearchState.Content(emptyList()))
    }

    fun addToHistory(track: Track) {
        tracksHistoryInteractor.addToHistory(track)
    }

    fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)
        }

        tracksInteractor.searchTracks(newSearchText, object : TracksInteractor.TracksConsumer {
            override fun consume(
                foundTracks: List<Track>?,
                errorMessage: String?
            ) {
                handler.post {
                    val tracks = mutableListOf<Track>()
                    if (foundTracks != null) {
                        tracks.addAll(foundTracks)
                    }

                    when {
                        errorMessage != null -> renderState(
                            SearchState.Error(context.resources.getString(R.string.connection_problems))
                        )

                        tracks.isEmpty() -> renderState(
                            SearchState.Empty(context.resources.getString(R.string.nothing_found))
                        )
                        else -> renderState(
                            SearchState.Content(tracks)
                        )
                    }
                }
            }
        })
    }

    fun loadHistory() {
        tracksHistoryInteractor.getHistory(object : TracksHistoryInteractor.TracksConsumer {
            override fun consume(tracks: List<Track>?) {
                handler.post {
                    if (!tracks.isNullOrEmpty()) {
                        renderState(SearchState.History(tracks))
                    } else {
                        renderState(SearchState.Content(emptyList()))
                    }
                }
            }
        })
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }
}