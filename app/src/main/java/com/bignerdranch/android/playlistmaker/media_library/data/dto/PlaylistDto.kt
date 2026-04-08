package com.bignerdranch.android.playlistmaker.media_library.data.dto

data class PlaylistDto (
    val playlistId: Int = 0,
    val playlistName: String,
    val playlistDescription: String? = null,
    val imagePath: String? = null,
    val tracksIds: List<String>
)