package com.bignerdranch.android.playlistmaker.media_library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.bignerdranch.android.playlistmaker.media_library.presentation.EditPlaylistViewModel
import com.bignerdranch.android.playlistmaker.media_library.ui.CurrentPlaylistFragment.Companion.PLAYLIST_ID
import com.bignerdranch.android.playlistmaker.media_library.ui.models.CreatePlaylistState
import com.bignerdranch.android.playlistmaker.util.Converter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : CreatePlaylistFragment() {

    private val playlistID: Int by lazy {
        arguments?.getInt(PLAYLIST_ID) ?: 0
    }
    override val viewModel by viewModel<EditPlaylistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.twNewPlaylistTitle.text = "Редактировать плейлист"
        binding.btnCreate.text = "Сохранить"

        backCallback.remove()

        viewModel.processState(playlistID)

        viewModel.observeCreatePlaylistState.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }

        binding.btnCreate.setOnClickListener {
            viewModel.editPlaylist(playlistID)
            Toast.makeText(
                requireContext(),
                "Плейлист изменен",
                Toast.LENGTH_LONG)
                .show()
            findNavController().popBackStack()
        }
    }

    private fun renderState(state: CreatePlaylistState) {
        binding.imgViewTrackImage.setImageResource(R.drawable.ic_add_playlist)

        state.coverUri?.let { uri ->
            Glide.with(this)
                .load(uri)
                .centerCrop()
                .transform(RoundedCorners(Converter.dpToPx(8f, requireContext())))
                .into(binding.imgViewTrackImage)

            binding.imgViewTrackImage.setTag(R.id.tag_image_loaded, true)
        } ?: run {
            binding.imgViewTrackImage.setTag(R.id.tag_image_loaded, false)
        }

        if (binding.editTextName.text.toString() != state.title) {
            binding.editTextName.setText(state.title)
        }

        if (binding.editTextDescription.text.toString() != state.description) {
            binding.editTextDescription.setText(state.description)
        }
    }
}