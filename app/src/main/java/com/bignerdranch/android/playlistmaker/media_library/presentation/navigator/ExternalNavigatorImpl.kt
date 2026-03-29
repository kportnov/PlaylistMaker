package com.bignerdranch.android.playlistmaker.media_library.presentation.navigator

import android.content.Context
import android.content.Intent
import com.bignerdranch.android.playlistmaker.R

class ExternalNavigatorImpl(private val context: Context)
    : ExternalNavigator{


    override fun sharePlaylist(text: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, text)
        val intentChooser = Intent.createChooser(intent, context.getString(R.string.share_via))
        intentChooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intentChooser)
    }
}