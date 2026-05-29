package com.bignerdranch.android.playlistmaker.player.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.player.presentation.AudioPlayerControl
import com.bignerdranch.android.playlistmaker.player.presentation.NotificationPlayerControl
import com.bignerdranch.android.playlistmaker.player.ui.model.PlayerState
import com.bignerdranch.android.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.bignerdranch.android.playlistmaker.util.Converter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class PlayerService : Service(), AudioPlayerControl, NotificationPlayerControl {

    private val binder = PlayerServiceBinder()
    private val mediaPlayer: MediaPlayer by inject()
    private val tracksHistoryInteractor: TracksHistoryInteractor by inject()

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Default())
    val playerState = _playerState.asStateFlow()

    private var timerJob: Job? = null


    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onBind(p0: Intent?): IBinder? {
        CoroutineScope(Dispatchers.Default).launch {
            val history = tracksHistoryInteractor.getHistory().firstOrNull()
            val track = history?.firstOrNull()
            _playerState.value = PlayerState.Default(track)
            initMediaPlayer()
        }

        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        releasePlayer()
        return super.onUnbind(intent)
    }

    override fun getState(): StateFlow<PlayerState> {
        return playerState
    }

    override fun startPlayer() {
        mediaPlayer.start()
        val track = _playerState.value.track
        track?.let {
            _playerState.value = PlayerState.Playing(it,getCurrentPlayerPosition())
        }
        startTimer()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        timerJob?.cancel()
        val track = _playerState.value.track
        track?.let {
            _playerState.value = PlayerState.Paused(it,getCurrentPlayerPosition())
        }
    }

    private fun initMediaPlayer() {
        val track = _playerState.value.track
        mediaPlayer.setDataSource(track?.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            track?.let {
                _playerState.value = PlayerState.Prepared(it)
            }
        }
        mediaPlayer.setOnCompletionListener {
            timerJob?.cancel()
            track?.let {
                _playerState.value = PlayerState.Prepared(it)
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (_playerState.value.isPlaying) {
                delay(300L)
                val track = _playerState.value.track
                track?.let {
                    _playerState.value = PlayerState.Playing(it,getCurrentPlayerPosition())
                }
            }
        }
    }

    private fun releasePlayer() {
        mediaPlayer.stop()
        mediaPlayer.release()
        _playerState.value = PlayerState.Default()
    }

    private fun getCurrentPlayerPosition(): String {
        return Converter.longToMMSS(mediaPlayer.currentPosition.toLong())
    }


    override fun showNotification() {
        ServiceCompat.startForeground(
            this,
            SERVICE_NOTIFICATION_ID,
            createServiceNotification(),
            getForegroundServiceTypeConstant()
        )
    }

    override fun hideNotification() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    private fun createServiceNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.playlist_maker))
            .setContentText("${_playerState.value.track?.artistName} - ${_playerState.value.track?.trackName}")
            .setSmallIcon(com.bignerdranch.android.playlistmaker.R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun createNotificationChannel() {
        // Создание каналов доступно только с Android 8.0
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val channel = NotificationChannel(
            /* id= */ NOTIFICATION_CHANNEL_ID,
            /* name= */ getString(R.string.playerservice),
            /* importance= */ NotificationManager.IMPORTANCE_LOW
        )
        channel.description = getString(R.string.service_for_playing_music)

        // Регистрируем канал уведомлений
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun getForegroundServiceTypeConstant(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        } else {
            0
        }
    }

    inner class PlayerServiceBinder : Binder() {
        fun getService(): PlayerService = this@PlayerService
    }

    private companion object {
        const val LOG_TAG = "PlayerService"
        const val NOTIFICATION_CHANNEL_ID = "player_service_channel"
        const val SERVICE_NOTIFICATION_ID = 100
    }
}