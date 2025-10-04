package com.bignerdranch.android.playlistmaker

import com.google.gson.annotations.SerializedName

data class Track (
    val trackName: String,
    val artistName: String,
    @SerializedName("trackTimeMillis") val trackTime: String?,
    @SerializedName("artworkUrl100") val artworkUrl: String?,
    val trackId: String
)