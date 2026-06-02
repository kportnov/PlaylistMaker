package com.bignerdranch.android.playlistmaker.search.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.search.presentation.SearchViewModel
import com.bignerdranch.android.playlistmaker.theme.ButtonTextStyle
import com.bignerdranch.android.playlistmaker.theme.TextErrorStyle

@Composable
fun ConnectionErrorScreen(
    viewModel: SearchViewModel,
    searchText: String
) {

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(R.drawable.img_connection_failure),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 110.dp)
                .size(120.dp)
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
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Button(
            onClick = { viewModel.searchRequest(searchText) },

            modifier = Modifier.padding(top = 24.dp),

            shape = RoundedCornerShape(54.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground
            )
        ) {

            Text(
                text = stringResource(R.string.update),
                style = ButtonTextStyle,
                color = MaterialTheme.colorScheme.background
            )
        }
    }
}