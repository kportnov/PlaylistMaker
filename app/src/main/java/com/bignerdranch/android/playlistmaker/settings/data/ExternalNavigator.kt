package com.bignerdranch.android.playlistmaker.settings.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.bignerdranch.android.playlistmaker.R

class ExternalNavigator(private val context: Context) {

    fun shareTheApp() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.https_practicum_yandex_ru_android_developer))
        val intentChooser = Intent.createChooser(intent, context.getString(R.string.share_via))
        intentChooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intentChooser)
    }

    fun sendToSupport() {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.myEmail)))
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.email_subject))
        intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.email_text))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        context.startActivity(intent)
    }

    fun openTerms() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(context.getString(R.string.https_yandex_ru_legal_practicum_offer_ru))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        context.startActivity(intent)
    }

}