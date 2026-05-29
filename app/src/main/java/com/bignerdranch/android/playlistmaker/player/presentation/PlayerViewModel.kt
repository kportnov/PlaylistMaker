package com.bignerdranch.android.playlistmaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.playlistmaker.media_library.domain.db.FavoritesInteractor
import com.bignerdranch.android.playlistmaker.player.ui.model.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val favoritesInteractor: FavoritesInteractor,
    ) : ViewModel() {


    private var playerJob: Job? = null
    private var audioPlayerControl: AudioPlayerControl? = null
    private var notificationControl: NotificationPlayerControl? = null
    private var isInForeground = true
    private var permissionNotificationGranted = false

    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    fun setAudioPlayerControl(audioPlayerControl: AudioPlayerControl) {
        this.audioPlayerControl = audioPlayerControl

        playerJob?.cancel()

        playerJob = viewModelScope.launch {
            audioPlayerControl.getState().collect {
                playerStateLiveData.postValue(it)
                onPlaybackChanged(it)
            }
        }
    }

    fun setNotificationControl(notificationPlayerControl: NotificationPlayerControl) {
        this.notificationControl = notificationPlayerControl
    }

    fun removePlayerControl() {
        playerJob?.cancel()
        playerJob = null

        audioPlayerControl = null
        notificationControl = null
    }

    private fun updateNotificationState(state: PlayerState) {

        if (!permissionNotificationGranted) return

        val shouldShow = state is PlayerState.Playing && !isInForeground

        if (shouldShow) {
            notificationControl?.showNotification()
        } else {
            notificationControl?.hideNotification()
        }
    }


    // Updates from UI
    fun onScreenForeground() {
        isInForeground = true
        playerStateLiveData.value?.let { updateNotificationState(it) }
    }

    // Updates from UI
    fun onScreenBackground() {
        isInForeground = false
        playerStateLiveData.value?.let { updateNotificationState(it) }
    }

    // Updates from PlayerState
    private fun onPlaybackChanged(state: PlayerState) {
        updateNotificationState(state)
    }

    fun grantPermission() {
        this.permissionNotificationGranted = true
    }

    fun onPlayButtonClicked() {
        when(playerStateLiveData.value) {
            is PlayerState.Playing -> audioPlayerControl?.pausePlayer()
            is PlayerState.Prepared, is PlayerState.Paused -> audioPlayerControl?.startPlayer()
            else -> {}
        }
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
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayerControl = null
        notificationControl = null
    }
}