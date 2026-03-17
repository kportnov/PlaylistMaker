package com.bignerdranch.android.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.databinding.FragmentPlayerBinding
import com.bignerdranch.android.playlistmaker.media_library.domain.models.Playlist
import com.bignerdranch.android.playlistmaker.player.presentation.BottomSheetViewModel
import com.bignerdranch.android.playlistmaker.player.presentation.PlayerViewModel
import com.bignerdranch.android.playlistmaker.player.ui.model.AddTrackState
import com.bignerdranch.android.playlistmaker.player.ui.model.PlaylistsUiState
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.util.Converter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class PlayerFragment: Fragment() {

    private lateinit var binding: FragmentPlayerBinding
    private val playerViewModel: PlayerViewModel by viewModel()
    private val bottomSheetViewModel: BottomSheetViewModel by viewModel()

    private lateinit var adapterBottomSheet: PlaylistsBSAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playerViewModel.observePlayerState().observe(viewLifecycleOwner) { state ->
            setTrackData(state.track)
            binding.btnPlayPause.apply {
                isEnabled = state.isPlayButtonEnabled
                setImageDrawable(AppCompatResources.getDrawable(context,state.buttonImageId))
            }
            binding.textViewCurrentTime.text = state.progress
            setFavoriteIcon(state.track?.isFavorite)
        }

        bottomSheetViewModel.getPlaylists()

        bottomSheetViewModel.observePlaylistLiveData().observe(viewLifecycleOwner) { state ->
            renderPlaylistState(state)
        }

        binding.btnPlayPause.setOnClickListener { playerViewModel.onPlayButtonClicked() }

        binding.imBtnFavorite.setOnClickListener {
            playerViewModel.onFavoriteClicked()
        }

        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
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

        binding.imBtnAddPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        adapterBottomSheet = PlaylistsBSAdapter { playlist ->
            bottomSheetViewModel.addTrackToPlaylist(playlist)
        }

        binding.recycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recycler.adapter = adapterBottomSheet

        binding.btnNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_createPlaylistFragment)
        }
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.onPause()
    }

    private fun setTrackData(track: Track?) {
        binding.apply {
            textViewTitle.text = track?.trackName
            textViewArtist.text = track?.artistName
            textViewPrimaryGenreNameValue.text = track?.primaryGenreName
            textViewCountryValue.text = track?.country
            setValueToTextView(textViewDurationValue, groupDuration, track?.trackDuration)
            setValueToTextView(textViewCollectionNameValue, groupCollectionName, track?.collectionName)
            setValueToTextView(textViewReleaseDateValue, groupReleaseDate, track?.releaseDate)
            setFavoriteIcon(track?.isFavorite)
            Glide.with(requireContext())
                .load(Converter.getCoverArtwork(track?.artworkUrl))
                .placeholder(R.drawable.img_placeholder)
                .centerInside()
                .transform(RoundedCorners(Converter.dpToPx(8f, requireContext())))
                .into(imgViewTrackImage)
        }
    }

    private fun setValueToTextView(textView: TextView, group: Group, value: String?) {
        group.isVisible = !value.isNullOrEmpty()
        textView.text = value
    }

    private fun setFavoriteIcon(isFavorite: Boolean?) {
        if (isFavorite == true) {
            binding.imBtnFavorite.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_favorite_clicked))
        } else {
            binding.imBtnFavorite.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_favorite))
        }
    }

    private fun updatePlaylists(playlists: List<Playlist>) {
        adapterBottomSheet.playlists.clear()
        adapterBottomSheet.playlists.addAll(playlists)
        adapterBottomSheet.notifyDataSetChanged()
    }

    private fun renderPlaylistState(state: PlaylistsUiState) {
        when (state) {
            is PlaylistsUiState.Content -> updatePlaylists(state.playlists)
            is PlaylistsUiState.AddTrackStatus -> {
                renderAddTrackStatus(state)
            }
        }
    }

    private fun renderAddTrackStatus(status: PlaylistsUiState.AddTrackStatus) {
        when (status.state) {
            is AddTrackState.AlreadyExists -> {
                Toast.makeText(requireContext(), "Трек уже добавлен в плейлист ${status.state.playlist.playlistName}", Toast.LENGTH_LONG).show()
            }
            is AddTrackState.Added -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                Toast.makeText(requireContext(), "Добавлено в плейлист ${status.state.playlist.playlistName}", Toast.LENGTH_LONG).show()
            }
        }
    }
}