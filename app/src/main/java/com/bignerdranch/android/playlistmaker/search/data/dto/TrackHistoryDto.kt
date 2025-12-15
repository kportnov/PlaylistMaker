package com.bignerdranch.android.playlistmaker.search.data.dto

import com.google.gson.annotations.SerializedName

data class TrackHistoryDto (
    val trackName: String,
    val artistName: String,
    @SerializedName("trackTimeMillis") val trackDuration: String?,
    @SerializedName("artworkUrl100") val artworkUrl: String?,
    val trackId: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
)