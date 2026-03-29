package com.bignerdranch.android.playlistmaker.media_library.ui.models

import com.bignerdranch.android.playlistmaker.search.domain.models.Track

data class PlaylistCurrentState(
    val id: Int,
    val title: String = "",
    val description: String? = null,
    val imagePath: String? = null,
    val duration: String = "0",
    val tracks: List<Track> = emptyList()
)