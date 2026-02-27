package com.bignerdranch.android.playlistmaker.media_library.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class TrackEntity(
    @PrimaryKey
    val trackId: String,
    val artworkUrl: String?,
    val trackName: String,
    val artistName: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String,
    val country: String,
    val trackDuration: String?,
    val previewUrl: String
)