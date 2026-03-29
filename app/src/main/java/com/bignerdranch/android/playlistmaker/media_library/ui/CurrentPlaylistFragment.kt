package com.bignerdranch.android.playlistmaker.media_library.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.databinding.FragmentCurrentPlaylistBinding
import com.bignerdranch.android.playlistmaker.media_library.presentation.CurrentPlaylistViewModel
import com.bignerdranch.android.playlistmaker.media_library.ui.adapter.MediaLibraryTrackAdapter
import com.bignerdranch.android.playlistmaker.media_library.ui.models.PlaylistCurrentState
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.util.Converter
import com.bignerdranch.android.playlistmaker.util.debounce
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import kotlin.getValue

class CurrentPlaylistFragment: Fragment() {

    private val playlistID: Int by lazy {
        arguments?.getInt(PLAYLIST_ID) ?: 0
    }
    private lateinit var binding: FragmentCurrentPlaylistBinding
    private val viewModel by viewModel<CurrentPlaylistViewModel>()
    private lateinit var bottomSheetBehaviorTracks: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetBehaviorMenu: BottomSheetBehavior<LinearLayout>

    private lateinit var mediaLibraryTrackAdapter: MediaLibraryTrackAdapter
    private lateinit var onTrackClickDebounce: (Track) -> Unit




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCurrentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.processPlaylist(playlistID)

        viewModel.observeLiveData().observe(viewLifecycleOwner) { state ->
                renderState(state)
        }

        onTrackClickDebounce = debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            viewModel.addToHistory(track)
            findNavController().navigate(R.id.action_playlistCurrentFragment_to_playerFragment)
        }

        mediaLibraryTrackAdapter = MediaLibraryTrackAdapter(
            object : MediaLibraryTrackAdapter.MediaLibraryTrackAdapterClickListener {
            override fun onTrackClick(track: Track) {
                onTrackClickDebounce(track)
            }

            override fun onLongTrackClick(track: Track) {
                MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                    .setTitle(getString(R.string.do_you_like_to_delete_the_track))
                    .setNegativeButton(getString(R.string.no)) { _, _ ->

                    }.setPositiveButton(getString(R.string.yes)) { _, _ ->
                        viewModel.deleteTrackFromPlaylist(track.id, playlistID)
                    }.show()
            }
        })

        binding.recyclerTracks.adapter = mediaLibraryTrackAdapter
        binding.recyclerTracks.layoutManager = LinearLayoutManager(requireContext())

        bottomSheetBehaviorTracks = BottomSheetBehavior.from(binding.tracksBottomSheetTracks)
        bottomSheetBehaviorMenu = BottomSheetBehavior.from(binding.tracksBottomSheetMenu)
        bottomSheetBehaviorMenu.state = BottomSheetBehavior.STATE_HIDDEN


        bottomSheetBehaviorTracks.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.overlay.visibility =
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = slideOffset.coerceIn(0f, 1f)
            }
        }
        )

        bottomSheetBehaviorMenu.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.overlay.visibility =
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = slideOffset.coerceIn(0f, 1f)
            }
        }
        )

        binding.imageButtonMenu.setOnClickListener {
            bottomSheetBehaviorMenu.state = BottomSheetBehavior.STATE_COLLAPSED
        }


        binding.buttonBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.imageButtonShare.setOnClickListener {
            shareThePlaylist()
        }

        binding.btnShare.setOnClickListener {
            shareThePlaylist()
        }

        binding.btnDeletePlaylist.setOnClickListener {
            val playlistName = viewModel.observeLiveData().value?.title ?: ""

            MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                .setTitle(getString(R.string.want_to_delete_playlist_name, playlistName))
                .setNegativeButton(getString(R.string.no)) { _, _ ->

                }.setPositiveButton(getString(R.string.yes)) { _, _ ->
                    lifecycleScope.launch {
                        viewModel.deletePlaylistById(playlistID)
                        findNavController().navigateUp()
                    }
                }.show()
        }

        binding.btnEditPlaylist.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(PLAYLIST_ID, playlistID)
            }
            findNavController().navigate(R.id.action_currentPlaylistFragment_to_editPlaylistFragment, bundle)
        }
    }

    private fun renderState(state: PlaylistCurrentState) {
        val tracksNumber = state.tracks.size
        val tracksText = requireContext().resources.getQuantityString(
            R.plurals.tracks_count,
            tracksNumber,
            tracksNumber)

        binding.textViewPlaylistTitle.text = state.title
        binding.textViewPlaylistDescription.isVisible = !state.description.isNullOrEmpty()
        binding.textViewPlaylistDescription.text = state.description
        binding.textViewPlaylistTracksNumber.text = tracksText
        binding.textViewPlaylistDuration.text = state.duration

        val file = File(state.imagePath ?: "")
        Glide.with(this)
            .load(Uri.fromFile(file))
            .placeholder(R.drawable.img_placeholder)
            .transform(CenterCrop())
            .into(binding.imgViewPlaylistImage)

        binding.playlistItem.textViewPlaylistNameLine.text = state.title
        binding.playlistItem.textViewTracksNumberLine.text = tracksText
        Glide.with(this)
            .load(Uri.fromFile(file))
            .placeholder(R.drawable.img_placeholder)
            .transform(CenterCrop(), RoundedCorners(Converter.dpToPx(2f, requireContext())))
            .into(binding.playlistItem.imgViewPlaylistImageLine)

        mediaLibraryTrackAdapter.trackList.clear()
        mediaLibraryTrackAdapter.trackList.addAll(state.tracks)
        mediaLibraryTrackAdapter.notifyDataSetChanged()

        binding.recyclerTracks.isVisible = !state.tracks.isEmpty()
        binding.textViewNoTracks.isVisible = state.tracks.isEmpty()
    }

    private fun shareThePlaylist() {
        if (mediaLibraryTrackAdapter.trackList.isNotEmpty()) {
            viewModel.shareThePlaylist(playlistID)
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.no_tracks_to_share),
                Toast.LENGTH_LONG)
                .show()
        }
        bottomSheetBehaviorMenu.state = BottomSheetBehavior.STATE_HIDDEN
    }

    companion object {
        const val PLAYLIST_ID = "PLAYLIST_ID"
        const val CLICK_DEBOUNCE_DELAY = 500L
    }
}