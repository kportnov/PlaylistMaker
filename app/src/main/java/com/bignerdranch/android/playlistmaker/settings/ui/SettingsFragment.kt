package com.bignerdranch.android.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.bignerdranch.android.playlistmaker.theme.PlaylistMakerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setContent {
                PlaylistMakerTheme {
                    SettingsScreen(
                        isDarkTheme = viewModel.getTheme() ?: false,
                        onThemeClick = {
                            viewModel.manageTheme(it)
                        },
                        onShareClick = {
                            viewModel.shareApp()
                        },
                        onSupportClick = {
                            viewModel.openSupport()
                        },
                        onTermsClick = {
                            viewModel.openTerms()
                        }
                    )
                }
            }
        }
    }
}