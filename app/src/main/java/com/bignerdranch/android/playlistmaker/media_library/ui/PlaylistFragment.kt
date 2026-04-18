package com.bignerdranch.android.playlistmaker.media_library.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.databinding.FragmentPlaylistBinding
import com.bignerdranch.android.playlistmaker.main.ui.MainActivity
import com.bignerdranch.android.playlistmaker.media_library.domain.models.Playlist
import com.bignerdranch.android.playlistmaker.media_library.presentation.PlaylistsViewModel
import com.bignerdranch.android.playlistmaker.media_library.ui.adapter.PlaylistsAdapter
import com.bignerdranch.android.playlistmaker.media_library.ui.models.PlaylistsState
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PlaylistsViewModel>()
    private lateinit var playlistsAdapter: PlaylistsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistsAdapter = PlaylistsAdapter { playlist ->
            val bundle = Bundle().apply {
                putInt(PLAYLIST_ID, playlist.playlistId)
            }
            (activity as MainActivity).animateBottomNavigationView()
            findNavController().navigate(
                R.id.action_mediaLibraryFragment_to_playlistCurrentFragment,
                bundle
            )
        }
        binding.recyclerView.adapter = playlistsAdapter

        viewModel.getPlaylists()

        viewModel.observeLiveData().observe(viewLifecycleOwner) { state ->
            render(state)
        }

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        binding.btnNewPlaylist.setOnClickListener {
            (activity as MainActivity).animateBottomNavigationView()
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_createPlaylistFragment)
        }
    }



    private fun showEmpty() {
        binding.error.viewGroupError.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        context?.let {
            binding.error.apply {
                imageViewError.setImageDrawable(
                    AppCompatResources.getDrawable(
                        it,
                        R.drawable.img_nothing_found
                    )
                )
                textViewMessage.text = getString(R.string.no_playlist_has_been_created)
            }
        }
    }

    private fun showPlaylists(playlists: List<Playlist>) {
        playlistsAdapter.playlists.clear()
        playlistsAdapter.playlists.addAll(playlists)
        playlistsAdapter.notifyDataSetChanged()

        binding.error.viewGroupError.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Empty -> showEmpty()
            is PlaylistsState.Content -> showPlaylists(state.playlists)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val PLAYLIST_ID = "PLAYLIST_ID"

        @JvmStatic
        fun newInstance() =
            PlaylistFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}