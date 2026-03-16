package com.bignerdranch.android.playlistmaker.media_library.ui.models

import android.net.Uri

data class CreatePlaylistState(
    val title: String = "",
    val description: String? = null,
    val coverUri: Uri? = null
)