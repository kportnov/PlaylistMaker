package com.bignerdranch.android.playlistmaker.media_library.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.bignerdranch.android.playlistmaker.media_library.presentation.CreatePlaylistViewModel
import com.bignerdranch.android.playlistmaker.media_library.presentation.PlaylistsViewModel
import com.bignerdranch.android.playlistmaker.util.Converter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import kotlin.getValue

class CreatePlaylistFragment: Fragment() {


    private lateinit var binding: FragmentCreatePlaylistBinding
    private val viewModel by viewModel<CreatePlaylistViewModel>()

    lateinit var confirmDialog: MaterialAlertDialogBuilder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeCreatePlaylistState.observe(viewLifecycleOwner) { state ->

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
        }

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                viewModel.updateCover(uri)
            }
        }

        binding.imgViewTrackImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.editTextName.doOnTextChanged { text, start, before, count ->
            binding.btnCreate.isEnabled = !text.isNullOrEmpty()
            viewModel.updateTitle(text.toString())
        }
        binding.editTextDescription.doOnTextChanged { text, start, before, count ->
            viewModel.updateDescription(text.toString())
        }

        confirmDialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNeutralButton("Отмена") { dialog, which ->

            }.setPositiveButton("Завершить") { dialog, which ->
                findNavController().popBackStack()
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val isImageLoaded = binding.imgViewTrackImage.getTag(R.id.tag_image_loaded) == true

            val dialogIsShown =
                isImageLoaded ||
                        !binding.editTextName.text.isNullOrEmpty() ||
                        !binding.editTextDescription.text.isNullOrEmpty()

            if (dialogIsShown) {
                confirmDialog.show()
            } else {
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        binding.btnCreate.setOnClickListener {
            viewModel.createPlaylist()
            Toast.makeText(
                requireContext(),
                "Плейлист ${viewModel.observeCreatePlaylistState.value?.title} создан",
                Toast.LENGTH_LONG)
                .show()
            findNavController().popBackStack()
        }

        binding.buttonBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CreatePlaylistFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}