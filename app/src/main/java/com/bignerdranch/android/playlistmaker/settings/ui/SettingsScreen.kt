package com.bignerdranch.android.playlistmaker.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.theme.ButtonSettingsTextStyle
import com.bignerdranch.android.playlistmaker.theme.TitleTextStyle

@Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    onThemeClick: (Boolean) -> Unit,
    onShareClick: () -> Unit,
    onSupportClick: () -> Unit,
    onTermsClick: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Text(
            text = stringResource(R.string.settings),
            modifier = Modifier.padding(20.dp),
            style = TitleTextStyle,
            color = MaterialTheme.colorScheme.onBackground
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onThemeClick(!isDarkTheme)
                }
                .padding(
                    horizontal = 16.dp,
                    vertical = 10.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = stringResource(R.string.dark_theme),
                modifier = Modifier.weight(1f),
                style = ButtonSettingsTextStyle,
                color = MaterialTheme.colorScheme.onBackground
            )

            Switch(
                checked = isDarkTheme,
                onCheckedChange = {
                    onThemeClick(it)
                }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onShareClick)
                .padding(
                    horizontal = 16.dp,
                    vertical = 18.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = stringResource(R.string.share_the_app),
                modifier = Modifier.weight(1f),
                style = ButtonSettingsTextStyle,
                color = MaterialTheme.colorScheme.onBackground
            )

            Icon(
                painter = painterResource(R.drawable.ic_share_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onSupportClick)
                .padding(
                    horizontal = 16.dp,
                    vertical = 18.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = stringResource(R.string.send_to_support),
                modifier = Modifier.weight(1f),
                style = ButtonSettingsTextStyle,
                color = MaterialTheme.colorScheme.onBackground
            )

            Icon(
                painter = painterResource(R.drawable.ic_support_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onTermsClick)
                .padding(
                    horizontal = 16.dp,
                    vertical = 18.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = stringResource(R.string.user_agreement),
                modifier = Modifier.weight(1f),
                style = ButtonSettingsTextStyle,
                color = MaterialTheme.colorScheme.onBackground
            )

            Icon(
                painter = painterResource(R.drawable.ic_arrow_forward_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}