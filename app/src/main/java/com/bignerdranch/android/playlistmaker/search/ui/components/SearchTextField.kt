package com.bignerdranch.android.playlistmaker.search.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.theme.EditTextStyle
import com.bignerdranch.android.playlistmaker.theme.YPBlack
import com.bignerdranch.android.playlistmaker.theme.YPBlue

@Composable
fun SearchTextField(
    text: String,
    onTextChanged: (String) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource(R.drawable.ic_search_24),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 12.dp)
        )

        BasicTextField(
            value = text,
            onValueChange = onTextChanged,
            singleLine = true,
            textStyle = EditTextStyle.copy(
                color = YPBlack
            ),
            cursorBrush = SolidColor(YPBlue),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            decorationBox = { innerTextField ->

                if (text.isEmpty()) {
                    Text(
                        text = stringResource(R.string.search),
                        style = EditTextStyle,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                innerTextField()
            }
        )

        if (text.isNotEmpty()) {

            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .clickable { onTextChanged("") }
            )
        }
    }
}