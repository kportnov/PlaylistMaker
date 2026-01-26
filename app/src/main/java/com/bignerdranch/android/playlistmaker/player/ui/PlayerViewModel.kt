package com.bignerdranch.android.playlistmaker.player.ui

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bignerdranch.android.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.util.Converter

class PlayerViewModel(
    private val tracksHistoryInteractor: TracksHistoryInteractor
    ) : ViewModel() {

    private val playerStateLiveData = MutableLiveData(STATE_DEFAULT)
    fun observePlayerState(): LiveData<Int> = playerStateLiveData

    private val progressTimeLiveData = MutableLiveData(Converter.longToMMSS(0))
    fun observeProgressTime(): LiveData<String?> = progressTimeLiveData

    private val trackLiveData = MutableLiveData(getLastTrack())
    fun observeTrackLiveData(): LiveData<Track?> = trackLiveData

    private val mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())

    private val timerRunnable = Runnable {
        if (playerStateLiveData.value == STATE_PLAYING) {
            startTimerUpdate()
        }
    }

    init {
        preparePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        resetTimer()
    }

    fun onPlayButtonClicked() {
        when(playerStateLiveData.value) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(getLastTrack()?.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(STATE_PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            playerStateLiveData.postValue(STATE_PREPARED)
            resetTimer()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerStateLiveData.postValue(STATE_PLAYING)
        startTimerUpdate()
    }

    private fun pausePlayer() {
        pauseTimer()
        mediaPlayer.pause()
        playerStateLiveData.postValue(STATE_PAUSED)
    }

    private fun startTimerUpdate() {
        progressTimeLiveData.postValue(Converter.longToMMSS(mediaPlayer.currentPosition.toLong()))
        handler.postDelayed(timerRunnable, 200)
    }

    private fun pauseTimer() {
        handler.removeCallbacks(timerRunnable)
    }

    private fun resetTimer() {
        handler.removeCallbacks(timerRunnable)
        progressTimeLiveData.postValue(Converter.longToMMSS(0))
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

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
    }
}