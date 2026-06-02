package com.bignerdranch.android.playlistmaker.search.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.theme.SongArtistSearchStyle
import com.bignerdranch.android.playlistmaker.theme.SongNameSearchStyle
import com.bignerdranch.android.playlistmaker.util.Converter

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
            contentScale = ContentScale.Crop,
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
                style = SongNameSearchStyle,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row {
                Text(
                    text = track.artistName,
                    maxLines = 1,
                    style = SongArtistSearchStyle,
                    color = MaterialTheme.colorScheme.secondary
                )

                Icon(
                    modifier = Modifier.padding(horizontal = 2.dp),
                    painter = painterResource(R.drawable.ic_dot),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
                if (track.trackDuration != null) {
                    Text(
                        text = Converter.longToMMSS(track.trackDuration.toLong()),
                        maxLines = 1,
                        style = SongArtistSearchStyle,
                        color = MaterialTheme.colorScheme.secondary
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