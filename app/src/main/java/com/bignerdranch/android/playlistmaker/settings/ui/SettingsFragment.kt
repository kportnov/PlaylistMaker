package com.bignerdranch.android.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigator
import com.bignerdranch.android.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingsFragment: Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: SettingsViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.themeSwitcher.isChecked = viewModel.getTheme() ?: false

        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
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

    }
}