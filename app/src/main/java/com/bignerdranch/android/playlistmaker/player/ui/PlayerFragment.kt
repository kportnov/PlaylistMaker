package com.bignerdranch.android.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.databinding.FragmentPlayerBinding
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.util.Converter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class PlayerFragment: Fragment() {

    private lateinit var binding: FragmentPlayerBinding
    private val viewModel: PlayerViewModel by viewModel()


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

        viewModel.observeTrackLiveData().observe(viewLifecycleOwner) {
            setTrackData(it)
        }

        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            binding.btnPlayPause.apply {
                isEnabled = it.isPlayButtonEnabled
                setImageDrawable(AppCompatResources.getDrawable(context,it.buttonImageId))
            }
            binding.textViewCurrentTime.text = it.progress
        }

        viewModel.observeFavoriteLiveData().observe(viewLifecycleOwner) {
            setFavoriteIcon(it)
        }

        binding.btnPlayPause.setOnClickListener { viewModel.onPlayButtonClicked() }

        binding.imBtnFavorite.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
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
}