package com.bignerdranch.android.playlistmaker.media_library.presentation.formatter

import android.content.Context
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.media_library.domain.models.Playlist
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.util.Converter

class PlaylistShareFormatter(private val context: Context) {

    fun format(playlist: Playlist, tracks: List<Track>): String {
        val sb = StringBuilder()

        sb.appendLine(playlist.playlistName)

        if (!playlist.playlistDescription.isNullOrBlank()) {
            sb.appendLine(playlist.playlistDescription)
        }

        val tracksText = context.resources.getQuantityString(
            R.plurals.tracks_count,
            tracks.size,
            tracks.size)

        sb.appendLine(tracksText)

        tracks.forEachIndexed { index, track ->
            val duration = track.trackDuration?.let {
                " (${Converter.longToMMSS(it.toLong())})"
            } ?: ""

            sb.appendLine(
                "${index + 1}. ${track.artistName} - ${track.trackName}$duration"
            )
        }
        return sb.toString()
    }

}