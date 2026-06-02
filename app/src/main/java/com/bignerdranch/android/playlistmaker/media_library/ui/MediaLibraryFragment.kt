package com.bignerdranch.android.playlistmaker.media_library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.main.ui.MainActivity
import com.bignerdranch.android.playlistmaker.media_library.presentation.FavoritesViewModel
import com.bignerdranch.android.playlistmaker.media_library.presentation.PlaylistsViewModel
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.theme.PlaylistMakerTheme
import com.bignerdranch.android.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class MediaLibraryFragment: Fragment() {

    private val favoritesViewModel: FavoritesViewModel by viewModel()
    private val playlistsViewModel by viewModel<PlaylistsViewModel>()
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        onTrackClickDebounce = debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            favoritesViewModel.addToHistory(track)
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_playerFragment)
        }

        return ComposeView(requireContext()).apply {
            setContent {
                val favoritesState = favoritesViewModel.observeLiveData().observeAsState().value
                val playlistState = playlistsViewModel.observeLiveData().observeAsState().value

                PlaylistMakerTheme {
                    MediaLibraryScreen(
                        favoritesState = favoritesState,
                        playlistsState = playlistState,
                        onTrackClick = { track ->
                            (activity as MainActivity).animateBottomNavigationView()
                            onTrackClickDebounce(track)
                        },
                        onNewPlaylistClick = {
                            (activity as MainActivity).animateBottomNavigationView()
                            findNavController().navigate(R.id.action_mediaLibraryFragment_to_createPlaylistFragment)
                        },
                        onPlaylistClick = { playlist ->
                            val bundle = Bundle().apply {
                                putInt(PLAYLIST_ID, playlist.playlistId)
                            }
                            (activity as MainActivity).animateBottomNavigationView()
                            findNavController().navigate(
                                R.id.action_mediaLibraryFragment_to_playlistCurrentFragment,
                                bundle
                            )
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        favoritesViewModel.getFavorites()
        playlistsViewModel.getPlaylists()
    }

    companion object {
        const val PLAYLIST_ID = "PLAYLIST_ID"
        private const val CLICK_DEBOUNCE_DELAY = 500L
    }
}