package com.bignerdranch.android.playlistmaker.search.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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
import com.bignerdranch.android.playlistmaker.search.ui.models.SearchState
import com.bignerdranch.android.playlistmaker.theme.EditTextStyle
import com.bignerdranch.android.playlistmaker.theme.LightSearchBar
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
        TextField(
            value = searchText,
            onValueChange = { newText ->
                viewModel.onTextChanged(newText)

                viewModel.searchDebounce(newText)

                if (newText.isEmpty()) {
                    viewModel.getHistory()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = {
                Text(
                    text = stringResource(R.string.search),
                    style = EditTextStyle,
                    color = MaterialTheme.colorScheme.secondary
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
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            viewModel.onTextChanged("")
                            viewModel.getHistory()
                        }
                    )
                }
            },
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                cursorColor = MaterialTheme.colorScheme.primary
            )
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