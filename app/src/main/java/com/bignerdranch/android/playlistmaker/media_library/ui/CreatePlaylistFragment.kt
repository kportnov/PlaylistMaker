package com.bignerdranch.android.playlistmaker.media_library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bignerdranch.android.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.bignerdranch.android.playlistmaker.media_library.presentation.PlaylistsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class CreatePlaylistFragment: Fragment() {


    private lateinit var binding: FragmentCreatePlaylistBinding
    private val viewModel by viewModel<PlaylistsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editTextName.addTextChangedListener { text ->
            binding.btnCreate.isEnabled = !text.isNullOrEmpty()
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