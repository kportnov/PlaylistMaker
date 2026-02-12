package com.bignerdranch.android.playlistmaker.player.ui.model

import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.util.Converter

sealed class PlayerState(val isPlayButtonEnabled: Boolean, val buttonImageId: Int, val progress: String) {

    class Default : PlayerState(false, R.drawable.ic_play, Converter.longToMMSS(0))

    class Prepared : PlayerState(true, R.drawable.ic_play, Converter.longToMMSS(0))

    class Playing(progress: String) : PlayerState(true, R.drawable.ic_pause, progress)

    class Paused(progress: String) : PlayerState(true, R.drawable.ic_play, progress)
}