package com.bignerdranch.android.playlistmaker.search.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.search.ui.models.SearchState
import com.bignerdranch.android.playlistmaker.theme.ButtonTextStyle
import com.bignerdranch.android.playlistmaker.theme.EditTextStyle
import com.bignerdranch.android.playlistmaker.theme.LightSearchBar
import com.bignerdranch.android.playlistmaker.theme.SongArtistSearchStyle
import com.bignerdranch.android.playlistmaker.theme.SongNameSearchStyle
import com.bignerdranch.android.playlistmaker.theme.TextErrorStyle
import com.bignerdranch.android.playlistmaker.theme.TitleTextStyle
import com.bignerdranch.android.playlistmaker.theme.YPBlack
import com.bignerdranch.android.playlistmaker.theme.YPBlue
import com.bignerdranch.android.playlistmaker.theme.YPLightGrey
import com.bignerdranch.android.playlistmaker.util.Converter


@Composable
fun SearchScreen(
    state: SearchState,
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    onTrackClick: (Track) -> Unit,
    onClearHistoryClick: () -> Unit,
    onUpdateClick: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.search),
            modifier = Modifier.padding(20.dp),
            style = TitleTextStyle
        )
        TextField(
            value = searchText,
            onValueChange = onSearchTextChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = {
                Text(
                    text = stringResource(R.string.search),
                    style = EditTextStyle
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_search_24),
                    contentDescription = null,
                    tint = LightSearchBar
                )
            },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    Icon(
                        painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                        contentDescription = null
                    )
                }
            },
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = YPLightGrey,
                unfocusedContainerColor = YPLightGrey,
                cursorColor = YPBlue
            )
        )
        when (state) {
            is SearchState.Loading -> CircularProgressIndicator()
            is SearchState.Content -> SearchItems(state.tracks, onTrackClick)
            is SearchState.History -> HistoryItems(state.tracks, onTrackClick, onClearHistoryClick)
            is SearchState.Error -> ConnectionErrorScreen(onUpdateClick)
            is SearchState.Empty -> NothingFoundScreen()
        }
    }
}

@Composable
fun SearchItems(
    tracks: List<Track>,
    onClick: (Track) -> Unit
) {
    LazyColumn {
        items(tracks) { track ->
            TrackItem(
                track = track,
                onClick = { onClick(track) }
            )
        }
    }
}

@Composable
fun HistoryItems(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    onClearHistoryClick: () -> Unit
    ) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Text(
            modifier = Modifier.padding(top = 42.dp, bottom = 20.dp),
            text = stringResource(R.string.you_have_been_searching),
            style = TextErrorStyle
        )

        SearchItems(
            tracks = tracks,
            onClick = onTrackClick
        )

        Button(
            onClick = onClearHistoryClick,
            modifier = Modifier.padding(top = 24.dp, bottom = 20.dp),
            shape = RoundedCornerShape(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = YPBlack
            )
        ) {
            Text(
                text = stringResource(R.string.clear_history),
                style = ButtonTextStyle
            )
        }

    }
}

@Composable
fun TrackItem(
    track: Track,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = 13.dp,
                vertical = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = track.artworkUrl,
            contentDescription = null,
            placeholder = painterResource(R.drawable.img_placeholder),
            error = painterResource(R.drawable.img_placeholder),
            contentScale = ContentScale.Inside,
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(2.dp))
        )


        Column(
            modifier = Modifier
                .weight(1f)
                .padding(
                    start = 8.dp,
                    end = 30.dp
                )
        ) {
            Text(
                text = track.trackName,
                maxLines = 1,
                style = SongNameSearchStyle
            )

            Row {
                Text(
                    text = track.artistName,
                    maxLines = 1,
                    style = SongArtistSearchStyle
                )

                Icon(
                    modifier = Modifier.padding(horizontal = 2.dp),
                    painter = painterResource(R.drawable.ic_dot),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
                if (track.trackDuration != null) {
                    Text(
                        text = "2:34",
                        maxLines = 1,
                        style = SongArtistSearchStyle
                    )
                }
            }
        }

        Icon(
            painter = painterResource(R.drawable.ic_arrow_forward_24),
            contentDescription = null,
            tint = Color.Unspecified
        )
    }
}

@Composable
fun ConnectionErrorScreen(
    onUpdateClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth(),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(R.drawable.img_connection_failure),
            contentDescription = null,

            modifier = Modifier.padding(top = 110.dp)
        )

        Text(
            text = stringResource(R.string.connection_problems),

            modifier = Modifier
                .padding(
                    top = 16.dp,
                    start = 24.dp,
                    end = 24.dp
                ),

            style = TextErrorStyle,

            textAlign = TextAlign.Center
        )

        Button(
            onClick = onUpdateClick,

            modifier = Modifier.padding(top = 24.dp),

            shape = RoundedCornerShape(54.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = YPBlack
            )
        ) {

            Text(
                text = stringResource(R.string.update),
                style = ButtonTextStyle
            )
        }
    }
}

@Composable
fun NothingFoundScreen() {

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(R.drawable.img_nothing_found),
            contentDescription = null,
            modifier = Modifier.padding(top = 110.dp)
        )

        Text(
            text = stringResource(R.string.nothing_found),
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    start = 24.dp,
                    end = 24.dp
                ),
            style = TextErrorStyle,
            textAlign = TextAlign.Center
        )
    }
}