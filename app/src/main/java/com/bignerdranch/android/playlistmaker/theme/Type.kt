package com.bignerdranch.android.playlistmaker.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.bignerdranch.android.playlistmaker.R

val TitleTextStyle = TextStyle(
        fontSize = 22.sp,
        fontFamily = FontFamily(Font(R.font.ys_display_medium)),
        fontWeight = FontWeight(500)
        )

val EditTextStyle = TextStyle(
    fontSize = 16.sp,
    fontFamily = FontFamily(Font(R.font.ys_display_regular)),
    color = YPTextGrey,
    fontWeight = FontWeight(400)
)

val SongNameSearchStyle = TextStyle(
    fontSize = 16.sp,
    fontFamily = FontFamily(Font(R.font.ys_display_regular)),
    color = YPBlack,
    fontWeight = FontWeight(400)
)

val SongArtistSearchStyle = TextStyle(
    fontSize = 11.sp,
    fontFamily = FontFamily(Font(R.font.ys_display_regular)),
    color = YPTextGrey,
    fontWeight = FontWeight(400)
)

val TextErrorStyle = TextStyle(
    fontSize = 19.sp,
    fontFamily = FontFamily(Font(R.font.ys_display_medium)),
    color = YPBlack,
    fontWeight = FontWeight(400)
)

val ButtonTextStyle = TextStyle(
    fontSize = 14.sp,
    fontFamily = FontFamily(
        Font(R.font.ys_display_medium)
    ),
    color = Color.White,
    letterSpacing = 0.sp
)