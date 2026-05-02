package com.bignerdranch.android.playlistmaker.player.presentation

import com.bignerdranch.android.playlistmaker.player.ui.model.PlayerState
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerControl {

    fun getState(): StateFlow<PlayerState>

    fun startPlayer()

    fun pausePlayer()

}