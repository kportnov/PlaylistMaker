package com.bignerdranch.android.playlistmaker.search.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.search.presentation.SearchViewModel
import com.bignerdranch.android.playlistmaker.search.ui.components.ConnectionErrorScreen
import com.bignerdranch.android.playlistmaker.search.ui.components.HistoryItems
import com.bignerdranch.android.playlistmaker.search.ui.components.LoadingScreen
import com.bignerdranch.android.playlistmaker.search.ui.components.NothingFoundScreen
import com.bignerdranch.android.playlistmaker.search.ui.components.SearchItems
import com.bignerdranch.android.playlistmaker.search.ui.components.SearchTextField
import com.bignerdranch.android.playlistmaker.search.ui.models.SearchState
import com.bignerdranch.android.playlistmaker.theme.TitleTextStyle


@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onTrackClick: (Track) -> Unit
) {

    val state = viewModel.observeState().observeAsState().value
    val searchText = viewModel.text.collectAsState().value
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.search),
            modifier = Modifier.padding(20.dp),
            style = TitleTextStyle,
            color = MaterialTheme.colorScheme.onBackground
        )
        SearchTextField(
            text = searchText,
            onTextChanged = { newText ->
                viewModel.onTextChanged(newText)
                viewModel.searchDebounce(newText)

                if (newText.isEmpty()) {
                    viewModel.getHistory()
                }
            }
        )
        when (state) {
            is SearchState.Loading -> LoadingScreen()
            is SearchState.Content -> SearchItems(state.tracks, onTrackClick)
            is SearchState.History -> HistoryItems(viewModel, state.tracks, onTrackClick)
            is SearchState.Error -> ConnectionErrorScreen(viewModel, searchText)
            is SearchState.Empty -> NothingFoundScreen(state.messageId)
            else -> { }
        }
    }
}