package com.bignerdranch.android.playlistmaker.util

import android.content.Context
import android.util.TypedValue
import java.text.SimpleDateFormat
import java.util.Locale

object Converter {
    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    fun longToMMSS(value: String?): String? {
        return if (!value.isNullOrEmpty()) {
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(value.toLong())
        } else null
    }

    fun dateToYear(value: String?): String? {
        return value?.take(4)
    }

    fun getCoverArtwork(artworkUrl100: String?): String? {
        return artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
    }
}