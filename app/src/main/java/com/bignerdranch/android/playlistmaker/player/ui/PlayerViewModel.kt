package com.bignerdranch.android.playlistmaker.player.ui

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.playlistmaker.player.ui.model.PlayerState
import com.bignerdranch.android.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.util.Converter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val tracksHistoryInteractor: TracksHistoryInteractor
    ) : ViewModel() {

    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private val trackLiveData = MutableLiveData(getLastTrack())
    fun observeTrackLiveData(): LiveData<Track?> = trackLiveData

    private val mediaPlayer = MediaPlayer()
    private var timerJob: Job? = null

    init {
        initMediaPlayer()
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
        mediaPlayer.setDataSource(getLastTrack()?.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(PlayerState.Prepared())
        }
        mediaPlayer.setOnCompletionListener {
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


    //GSON нельзя в PlayerActivity class, чтобы передать track?
    private fun getLastTrack(): Track? {
        var track: Track? = null
        tracksHistoryInteractor.getHistory(object : TracksHistoryInteractor.TracksConsumer {
            override fun consume(tracks: List<Track>?) {
                track = tracks?.get(0)
            }
        })
        return track
    }

    private fun getCurrentPlayerPosition(): String {
        return Converter.longToMMSS(mediaPlayer.currentPosition.toLong())
    }
}