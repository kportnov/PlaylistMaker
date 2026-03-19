package com.bignerdranch.android.playlistmaker.media_library.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.media_library.domain.db.FavoritesInteractor
import com.bignerdranch.android.playlistmaker.media_library.ui.models.FavoritesState
import com.bignerdranch.android.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class FavoritesViewModel(
    val favoritesInteractor: FavoritesInteractor,
    val tracksHistoryInteractor: TracksHistoryInteractor
): ViewModel() {
    private val favoritesLiveData = MutableLiveData<FavoritesState>()
    fun observeLiveData(): LiveData<FavoritesState> = favoritesLiveData

    fun getFavorites() {
        viewModelScope.launch {
            favoritesInteractor.getFavorites().collect {
                processFavorites(it)
            }
        }
    }

    private fun processFavorites(favoritesList: List<Track>?) {
        if (favoritesList == null || favoritesList.isEmpty()) {
            renderState(FavoritesState.Empty(R.string.media_library_is_empty))
        } else {
            renderState(FavoritesState.Content(favoritesList))
        }
    }

    private fun renderState(state: FavoritesState) {
        favoritesLiveData.postValue(state)
    }

    fun addToHistory(track: Track) {
        tracksHistoryInteractor.addToHistory(track)
    }


}