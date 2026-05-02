package com.bignerdranch.android.playlistmaker.player.ui.model

import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.util.Converter

sealed class PlayerState(val track: Track?, val isPlayButtonEnabled: Boolean, val isPlaying: Boolean, val progress: String, val name: String) {

    class Default(track: Track? = null) : PlayerState(track,false, false, Converter.longToMMSS(0), "DEFAULT")

    class Prepared(track: Track) : PlayerState(track,true, false, Converter.longToMMSS(0), "PREPARED")

    class Playing(track: Track, progress: String) : PlayerState(track, true, true, progress, "PLAYING")

    class Paused(track: Track, progress: String) : PlayerState(track,true, false, progress, "PAUSED")
}