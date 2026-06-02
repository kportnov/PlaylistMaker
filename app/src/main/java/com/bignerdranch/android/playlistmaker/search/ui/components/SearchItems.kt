package com.bignerdranch.android.playlistmaker.search.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import kotlinx.collections.immutable.ImmutableList

@Composable
fun SearchItems(
    tracks: ImmutableList<Track>,
    onTrackClick: (Track) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxSize()
    ) {

        items(
            items = tracks,
            key = { it.id },
            contentType = { "track" }
        ) { track ->
            TrackItem(
                track = track,
                onClick = { onTrackClick(track) }
            )
        }
    }
}

