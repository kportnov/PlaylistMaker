package com.bignerdranch.android.playlistmaker.search.ui

import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.main.ui.MainActivity
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.search.presentation.SearchViewModel
import com.bignerdranch.android.playlistmaker.search.ui.models.SearchState
import com.bignerdranch.android.playlistmaker.theme.PlaylistMakerTheme
import com.bignerdranch.android.playlistmaker.util.ConnectivityChangedReceiver
import com.bignerdranch.android.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue

class SearchFragment: Fragment() {
    private val viewModel: SearchViewModel by viewModel {
        parametersOf(context)
    }

    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private val connectivityChangedReceiver = ConnectivityChangedReceiver()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        onTrackClickDebounce = debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            viewModel.addToHistory(track)
            findNavController().navigate(R.id.action_searchFragment_to_playerFragment)
        }

        return ComposeView(requireContext()).apply {

            setContent {
                PlaylistMakerTheme() {
                    SearchScreen(
                        viewModel = viewModel,
                        onTrackClick = { track ->
                            (activity as MainActivity)
                                .animateBottomNavigationView()

                            onTrackClickDebounce(track)
                        }
                    )
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if (viewModel.observeState().value !is SearchState.Content) {
            viewModel.getHistory()
        }

        ContextCompat.registerReceiver(
            requireContext(),
            connectivityChangedReceiver,
            IntentFilter(ConnectivityChangedReceiver.ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED)
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(connectivityChangedReceiver)
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 500L
    }
    
}