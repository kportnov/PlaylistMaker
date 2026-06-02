package com.bignerdranch.android.playlistmaker.search.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.search.presentation.SearchViewModel
import com.bignerdranch.android.playlistmaker.theme.ButtonTextStyle
import com.bignerdranch.android.playlistmaker.theme.TextErrorStyle
import kotlinx.collections.immutable.ImmutableList

@Composable
fun HistoryItems(
    viewModel: SearchViewModel,
    tracks: ImmutableList<Track>,
    onTrackClick: (Track) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(top = 42.dp, bottom = 20.dp),
            text = stringResource(R.string.you_have_been_searching),
            style = TextErrorStyle,
            color = MaterialTheme.colorScheme.onBackground
        )

        SearchItems(
            tracks = tracks,
            onTrackClick = onTrackClick
        )

        Button(
            onClick = { viewModel.clearHistory() },
            modifier = Modifier.padding(top = 24.dp, bottom = 20.dp),
            shape = RoundedCornerShape(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground

            )
        ) {
            Text(
                text = stringResource(R.string.clear_history),
                style = ButtonTextStyle,
                color = MaterialTheme.colorScheme.background
            )
        }

    }
}
