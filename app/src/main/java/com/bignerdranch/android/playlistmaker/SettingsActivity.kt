package com.bignerdranch.android.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val flShareTheApp = findViewById<FrameLayout>(R.id.fl_share_the_app)
        val flSendToSupport = findViewById<FrameLayout>(R.id.fl_send_to_support)
        val flUserAgreement = findViewById<FrameLayout>(R.id.fl_user_agreement)
        val buttonBack = findViewById<Button>(R.id.button_back)

        flShareTheApp.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT,
                getString(R.string.https_practicum_yandex_ru_android_developer))
            startActivity(Intent.createChooser(intent, getString(R.string.share_via)))
        }

        flSendToSupport.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.myEmail)))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_text))
            startActivity(intent)
        }

        flUserAgreement.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(getString(R.string.https_yandex_ru_legal_practicum_offer_ru))
            startActivity(intent)
        }

        buttonBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

    }
}