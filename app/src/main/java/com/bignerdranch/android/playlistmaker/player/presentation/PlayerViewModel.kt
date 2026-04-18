package com.bignerdranch.android.playlistmaker.player.presentation

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.playlistmaker.media_library.domain.db.FavoritesInteractor
import com.bignerdranch.android.playlistmaker.player.ui.model.PlayerState
import com.bignerdranch.android.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.bignerdranch.android.playlistmaker.util.Converter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val mediaPlayer: MediaPlayer,
    private val tracksHistoryInteractor: TracksHistoryInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    ) : ViewModel() {

    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            val history = tracksHistoryInteractor.getHistory().firstOrNull()
            val track = history?.firstOrNull()
            playerStateLiveData.value = PlayerState.Default(track)
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
        val track = playerStateLiveData.value?.track
        mediaPlayer.setDataSource(track?.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            track?.let {
                playerStateLiveData.postValue(PlayerState.Prepared(it))
            }
        }
        mediaPlayer.setOnCompletionListener {
            timerJob?.cancel()
            track?.let {
                playerStateLiveData.postValue(PlayerState.Prepared(it))
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        val track = playerStateLiveData.value?.track
        track?.let {
            playerStateLiveData.postValue(PlayerState.Playing(it,getCurrentPlayerPosition()))
        }
        startTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        timerJob?.cancel()
        val track = playerStateLiveData.value?.track
        track?.let {
            playerStateLiveData.postValue(PlayerState.Paused(it,getCurrentPlayerPosition()))
        }
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
                val track = playerStateLiveData.value?.track
                track?.let {
                    playerStateLiveData.postValue(PlayerState.Playing(it,getCurrentPlayerPosition()))
                }
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
        val state = playerStateLiveData.value ?: return
        viewModelScope.launch {
            val track = state.track
            if (track != null) {
                val updatedTrack = track.copy(isFavorite = !track.isFavorite)
                if (updatedTrack.isFavorite) {
                    favoritesInteractor.addToFavorites(updatedTrack)
                } else {
                    favoritesInteractor.deleteFromFavorites(updatedTrack)
                }

                playerStateLiveData.value = when (state) {
                    is PlayerState.Default -> PlayerState.Default(updatedTrack)
                    is PlayerState.Prepared -> PlayerState.Prepared(updatedTrack)
                    is PlayerState.Playing -> PlayerState.Playing(updatedTrack, state.progress)
                    is PlayerState.Paused -> PlayerState.Paused(updatedTrack, state.progress)
                    else -> state
                }
            }
        }
    }
}