package com.bignerdranch.android.playlistmaker.media_library.ui.playlists

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.media_library.domain.models.Playlist
import com.bignerdranch.android.playlistmaker.media_library.ui.models.PlaylistsState
import com.bignerdranch.android.playlistmaker.search.ui.components.NothingFoundScreen
import com.bignerdranch.android.playlistmaker.theme.ButtonTextStyle

@Composable
fun PlaylistsScreen(
    playlistsState: PlaylistsState?,
    onPlaylistClick: (Playlist) -> Unit,
    onNewPlaylistClick: () -> Unit) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Button(
            onClick = onNewPlaylistClick,
            modifier = Modifier
                .padding(top = 24.dp, bottom = 24.dp)
                .align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground
            )
        ) {
            Text(
                text = stringResource(R.string.new_playlist),
                style = ButtonTextStyle,
                color = MaterialTheme.colorScheme.background
            )
        }

        when (playlistsState) {

            is PlaylistsState.Content -> {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {

                    items(
                        items = playlistsState.playlists,
                        key = { it.playlistId },
                        contentType = { "playlist" }
                        ) { playlist ->

                        PlaylistItem(
                            playlist = playlist,
                            onClick = {
                                onPlaylistClick(playlist)
                            }
                        )
                    }
                }
            }

            is PlaylistsState.Empty -> {
                NothingFoundScreen(playlistsState.messageId)
            }

            null -> {}
        }
    }
}