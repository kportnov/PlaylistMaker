package com.bignerdranch.android.playlistmaker.media_library.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.databinding.FragmentPlaylistBinding
import com.bignerdranch.android.playlistmaker.media_library.presentation.PlaylistsViewModel
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

        setEmptyMessage()
    }



    private fun setEmptyMessage() {
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

    companion object {
        @JvmStatic
        fun newInstance() =
            PlaylistFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}