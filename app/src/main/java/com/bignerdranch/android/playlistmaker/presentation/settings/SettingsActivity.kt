package com.bignerdranch.android.playlistmaker.presentation.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bignerdranch.android.playlistmaker.App
import com.bignerdranch.android.playlistmaker.R
import com.google.android.material.switchmaterial.SwitchMaterial

const val SHARED_PREFERENCES_SETTINGS = "preferences_settings"
const val NIGHT_MODE_KEY = "key_for_night_mode"

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

        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_SETTINGS, MODE_PRIVATE)

        val switchTheme = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        val btnShareTheApp = findViewById<Button>(R.id.btn_share_the_app)
        val btnSendToSupport = findViewById<Button>(R.id.btn_sent_to_support)
        val btnUserAgreement = findViewById<Button>(R.id.btn_user_agreement)
        val buttonBack = findViewById<ImageButton>(R.id.button_back)

        switchTheme.isChecked = sharedPreferences.getBoolean(NIGHT_MODE_KEY, false)
        
        switchTheme.setOnCheckedChangeListener { switcher, checked ->
            sharedPreferences.edit()
                .putBoolean(NIGHT_MODE_KEY ,checked)
                .apply()

            (applicationContext as App).switchTheme(checked)
        }

        btnShareTheApp.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT,
                getString(R.string.https_practicum_yandex_ru_android_developer))
            startActivity(Intent.createChooser(intent, getString(R.string.share_via)))
        }

        btnSendToSupport.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.myEmail)))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_text))
            startActivity(intent)
        }

        btnUserAgreement.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(getString(R.string.https_yandex_ru_legal_practicum_offer_ru))
            startActivity(intent)
        }

        buttonBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

    }
}