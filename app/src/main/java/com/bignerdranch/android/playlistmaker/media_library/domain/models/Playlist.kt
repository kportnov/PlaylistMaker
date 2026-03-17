package com.bignerdranch.android.playlistmaker.media_library.domain.models

data class Playlist (
    val playlistId: Int = 0,
    val playlistName: String,
    val playlistDescription: String? = null,
    val imagePath: String? = null,
    val tracksIds: List<Int>,
    val tracksNumber: Int = 0
)

