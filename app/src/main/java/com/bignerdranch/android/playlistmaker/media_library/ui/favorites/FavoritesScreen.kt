package com.bignerdranch.android.playlistmaker.media_library.ui.favorites

import androidx.compose.runtime.Composable
import com.bignerdranch.android.playlistmaker.media_library.ui.models.FavoritesState
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.search.ui.components.NothingFoundScreen
import com.bignerdranch.android.playlistmaker.search.ui.components.SearchItems

@Composable
fun FavoritesScreen(favoritesState: FavoritesState?, onTrackClick: (Track) -> Unit) {
    when(favoritesState) {
        is FavoritesState.Content -> {
            SearchItems(favoritesState.tracks, onTrackClick)
        }

        is FavoritesState.Empty -> {
            NothingFoundScreen(favoritesState.messageId)
        }

        null -> { }

    }
}