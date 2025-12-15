package com.bignerdranch.android.playlistmaker.settings.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.databinding.ActivitySettingsBinding
import com.bignerdranch.android.playlistmaker.settings.domain.api.SettingsInteractor
import com.bignerdranch.android.playlistmaker.settings.domain.api.SharingInteractor
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SettingsActivity : AppCompatActivity() {


    private lateinit var binding: ActivitySettingsBinding
    private val sharingInteractor: SharingInteractor by inject()
    private val settingsInteractor: SettingsInteractor by inject()
    private val viewModel: SettingsViewModel by viewModel {
        parametersOf(sharingInteractor, settingsInteractor)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.themeSwitcher.isChecked = viewModel.getTheme() ?: false

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.manageTheme(checked)
        }

        binding.btnShareTheApp.setOnClickListener {
            viewModel.shareApp()
        }

        binding.btnSentToSupport.setOnClickListener {
            viewModel.openSupport()
        }

        binding.btnUserAgreement.setOnClickListener {
            viewModel.openTerms()
        }

        binding.buttonBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }
}