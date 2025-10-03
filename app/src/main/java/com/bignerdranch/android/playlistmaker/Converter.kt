package com.bignerdranch.android.playlistmaker

import android.content.Context
import android.util.TypedValue
import java.text.SimpleDateFormat
import java.util.Locale

class Converter {
    companion object {
        fun dpToPx(dp: Float, context: Context): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.resources.displayMetrics).toInt()
        }

        fun longToMMSS(value: Long): String {
            return SimpleDateFormat("mm:ss", Locale.getDefault()).format(value)
        }
    }
}