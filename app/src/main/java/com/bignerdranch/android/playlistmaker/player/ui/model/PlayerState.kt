package com.bignerdranch.android.playlistmaker.player.ui.model

import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.util.Converter

sealed class PlayerState(val track: Track?, val isPlayButtonEnabled: Boolean, val buttonImageId: Int, val progress: String) {

    class Default(track: Track? = null) : PlayerState(track,false, R.drawable.ic_play, Converter.longToMMSS(0))

    class Prepared(track: Track) : PlayerState(track,true, R.drawable.ic_play, Converter.longToMMSS(0))

    class Playing(track: Track, progress: String) : PlayerState(track, true, R.drawable.ic_pause, progress)

    class Paused(track: Track, progress: String) : PlayerState(track,true, R.drawable.ic_play, progress)
}