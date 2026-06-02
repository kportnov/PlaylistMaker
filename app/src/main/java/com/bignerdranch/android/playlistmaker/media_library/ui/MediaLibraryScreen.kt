package com.bignerdranch.android.playlistmaker.media_library.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.media_library.domain.models.Playlist
import com.bignerdranch.android.playlistmaker.media_library.ui.favorites.FavoritesScreen
import com.bignerdranch.android.playlistmaker.media_library.ui.models.FavoritesState
import com.bignerdranch.android.playlistmaker.media_library.ui.models.PlaylistsState
import com.bignerdranch.android.playlistmaker.media_library.ui.playlists.PlaylistsScreen
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.theme.ButtonTextStyle
import com.bignerdranch.android.playlistmaker.theme.TitleTextStyle
import kotlinx.coroutines.launch

@Composable
fun MediaLibraryScreen(
    favoritesState: FavoritesState?,
    playlistsState: PlaylistsState?,
    onTrackClick: (Track) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onNewPlaylistClick: () -> Unit
    ) {

    val scope = rememberCoroutineScope()

    val pagerState = rememberPagerState(
        pageCount = { 2 }
    )

    val selectedTabIndex = pagerState.currentPage

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Text(
            text = stringResource(R.string.media_library),
            modifier = Modifier.padding(20.dp),
            style = TitleTextStyle,
            color = MaterialTheme.colorScheme.onBackground
        )

        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),
            indicator = { tabPositions ->

                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(
                        tabPositions[selectedTabIndex],
                    ),
                    height = 2.dp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) {

            Tab(
                selected = selectedTabIndex == 0,
                selectedContentColor = MaterialTheme.colorScheme.onBackground,
                unselectedContentColor = MaterialTheme.colorScheme.onBackground,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                },
                text = {
                    Text(
                        text = stringResource(R.string.favourites),
                        style = ButtonTextStyle,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight(500)
                        )
                }
            )

            Tab(
                selected = selectedTabIndex == 1,
                selectedContentColor = MaterialTheme.colorScheme.onBackground,
                unselectedContentColor = MaterialTheme.colorScheme.onBackground,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                },
                text = {
                    Text(
                        text = stringResource(R.string.playlists),
                        style = ButtonTextStyle,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight(500)
                        )
                }
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->

            when(page) {
                0 -> FavoritesScreen(favoritesState, onTrackClick)
                1 -> PlaylistsScreen(playlistsState, onPlaylistClick, onNewPlaylistClick)
            }
        }
    }
}