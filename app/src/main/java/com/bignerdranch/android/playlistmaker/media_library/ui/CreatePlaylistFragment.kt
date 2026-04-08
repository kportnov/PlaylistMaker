package com.bignerdranch.android.playlistmaker.media_library.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.bignerdranch.android.playlistmaker.media_library.presentation.CreatePlaylistViewModel
import com.bignerdranch.android.playlistmaker.util.Converter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

open class CreatePlaylistFragment: Fragment() {


    protected lateinit var binding: FragmentCreatePlaylistBinding
    protected open val viewModel by viewModel<CreatePlaylistViewModel>()
    protected lateinit var confirmDialog: MaterialAlertDialogBuilder
    protected lateinit var backCallback: OnBackPressedCallback

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

        binding.editTextName.doOnTextChanged { text, _, _, _ ->
            val value = text?.toString()?.trim().orEmpty()

            binding.btnCreate.isEnabled = value.isNotEmpty()
            viewModel.updateTitle(value)
        }
        binding.editTextDescription.doOnTextChanged { text, _, _, _ ->
            val value = text?.toString()?.trim().orEmpty()
            if (value != viewModel.observeCreatePlaylistState.value?.description) {
                viewModel.updateDescription(value)
            }
        }

        confirmDialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle(getString(R.string.confirm_checklist_is_completed))
            .setMessage(getString(R.string.all_data_will_be_lost))
            .setNeutralButton(getString(R.string.cancel)) { _, _ ->

            }.setPositiveButton(getString(R.string.finish)) { _, _ ->
                findNavController().popBackStack()
            }

        backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
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
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backCallback)

        binding.btnCreate.setOnClickListener {
            viewModel.createPlaylist()
            Toast.makeText(
                requireContext(),
                getString(
                    R.string.playlist_name_created,
                    viewModel.observeCreatePlaylistState.value?.title
                ),
                Toast.LENGTH_LONG)
                .show()
            findNavController().popBackStack()
        }

        binding.buttonBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
}