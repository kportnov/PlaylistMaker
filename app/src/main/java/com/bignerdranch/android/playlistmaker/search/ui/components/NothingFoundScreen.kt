package com.bignerdranch.android.playlistmaker.search.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.theme.TextErrorStyle

@Composable
fun NothingFoundScreen(messageId: Int) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(R.drawable.img_nothing_found),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 110.dp)
                .size(120.dp)
        )

        Text(
            text = stringResource(messageId),
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
    }
}