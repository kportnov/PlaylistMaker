package com.bignerdranch.android.playlistmaker.player.ui

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.playlistmaker.media_library.domain.db.FavoritesInteractor
import com.bignerdranch.android.playlistmaker.player.ui.model.PlayerState
import com.bignerdranch.android.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.util.Converter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val tracksHistoryInteractor: TracksHistoryInteractor,
    private val favoritesInteractor: FavoritesInteractor
    ) : ViewModel() {

    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private val trackLiveData = MutableLiveData<Track>()
    fun observeTrackLiveData(): LiveData<Track> = trackLiveData

    private val favoriteLiveData = MutableLiveData<Boolean>()
    fun observeFavoriteLiveData(): LiveData<Boolean> = favoriteLiveData

    private val mediaPlayer = MediaPlayer()
    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            tracksHistoryInteractor.getHistory().collect {
                trackLiveData.value = it[0]
            }
            initMediaPlayer()
        }
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    fun onPlayButtonClicked() {
        when(playerStateLiveData.value) {
            is PlayerState.Playing -> pausePlayer()
            is PlayerState.Prepared, is PlayerState.Paused -> startPlayer()
            else -> { }
        }
    }

    private fun initMediaPlayer() {
        mediaPlayer.setDataSource(trackLiveData.value?.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(PlayerState.Prepared())
        }
        mediaPlayer.setOnCompletionListener {
            timerJob?.cancel()
            playerStateLiveData.postValue(PlayerState.Prepared())
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerStateLiveData.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
        startTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        timerJob?.cancel()
        playerStateLiveData.postValue(PlayerState.Paused(getCurrentPlayerPosition()))
    }

    private fun releasePlayer() {
        mediaPlayer.stop()
        mediaPlayer.release()
        playerStateLiveData.value = PlayerState.Default()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(300L)
                playerStateLiveData.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
            }
        }
    }

    fun onPause() {
        pausePlayer()
    }

    private fun getCurrentPlayerPosition(): String {
        return Converter.longToMMSS(mediaPlayer.currentPosition.toLong())
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            val track = trackLiveData.value

            if (track != null) {
                val updatedTrack = track.copy(isFavorite = !track.isFavorite)
                if (updatedTrack.isFavorite) {
                    favoritesInteractor.addToFavorites(updatedTrack)
                } else {
                    favoritesInteractor.deleteFromFavorites(updatedTrack)
                }
                trackLiveData.value = updatedTrack

                // Теоретически, можно будет post весь updated track, тогда можно будет обойтись без favoriteLiveData
                // Но тогда будет перерисовывать всё. Не знаю, как лучше

                favoriteLiveData.postValue(updatedTrack.isFavorite)
            }
        }
    }
}