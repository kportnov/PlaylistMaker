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
import com.bignerdranch.android.playlistmaker.media_library.ui.models.PlaylistsState
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private lateinit var binding: FragmentPlaylistBinding
    private val viewModel by viewModel<PlaylistsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        binding.error.viewGroupError.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        binding.recyclerView.adapter = PlaylistsAdapter(playlists)
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Empty -> showEmpty()
            is PlaylistsState.Content -> showPlaylists(state.playlists)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PlaylistFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}