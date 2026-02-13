package com.bignerdranch.android.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.bignerdranch.android.playlistmaker.search.domain.api.TracksInteractor
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.search.ui.models.SearchState
import com.bignerdranch.android.playlistmaker.util.debounce
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val tracksHistoryInteractor: TracksHistoryInteractor,
    ): ViewModel() {

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private var latestSearchText: String? = null
    private val trackSearchDebounce = debounce<String>(
        SEARCH_DEBOUNCE_DELAY,
        viewModelScope,
        true
    ) { changedText ->
        searchRequest(changedText)
    }


    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }
        this.latestSearchText = changedText
        trackSearchDebounce(changedText)
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

            viewModelScope.launch {
                tracksInteractor.searchTracks(newSearchText).collect { pair ->
                    processResult(pair.first, pair.second)
                }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        val tracks = mutableListOf<Track>()
        if (foundTracks != null) {
            tracks.addAll(foundTracks)
        }
        when {
            errorMessage != null -> renderState(
                SearchState.Error(R.string.connection_problems)
            )

            tracks.isEmpty() -> renderState(
                SearchState.Empty(R.string.nothing_found)
            )
            else -> renderState(
                SearchState.Content(tracks)
            )
        }
    }

    fun loadHistory() {
        val historyList = mutableListOf<Track>()
        historyList.addAll(tracksHistoryInteractor.getHistory())
        if (historyList.isEmpty()) {
            renderState(SearchState.Content(historyList))
        } else {
            renderState(SearchState.History(historyList))
        }
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}