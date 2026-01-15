package com.bignerdranch.android.playlistmaker.media_library.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.databinding.FragmentFavoritesBinding
import com.bignerdranch.android.playlistmaker.media_library.presentation.FavoritesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class FavoritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private val viewModel: FavoritesViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setEmptyMessage()
    }

    private fun setEmptyMessage() {
        context?.let {
            binding.error.apply {
                btnNewPlaylist.visibility = View.INVISIBLE
                imageViewError.setImageDrawable(
                    AppCompatResources.getDrawable(
                        it,
                        R.drawable.img_nothing_found
                    )
                )
                textViewMessage.text = getString(R.string.media_library_is_empty)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FavoritesFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}