package com.bignerdranch.android.playlistmaker.player.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bignerdranch.android.playlistmaker.util.Converter
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.databinding.ActivityPlayerBinding
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson

const val KEY_PLAYER_ACTIVITY = "key player activity"

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private var viewModel: PlayerViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.constraint_activity_player)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this, PlayerViewModel.getFactory(getTrack()))
            .get(PlayerViewModel::class.java)

        viewModel?.observeTrackLiveData()?.observe(this) {
            setTrackData(it)
        }
        viewModel?.observePlayerState()?.observe(this) {
            changeButtonImg(it == PlayerViewModel.STATE_PLAYING)
            enableButton(it != PlayerViewModel.STATE_DEFAULT)
        }
        viewModel?.observeProgressTime()?.observe(this) {
            binding.textViewCurrentTime.text = it
        }

        binding.btnPlayPause.setOnClickListener { viewModel?.onPlayButtonClicked() }
        binding.buttonBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun getTrack(): Track {
        val json = intent.getStringExtra(KEY_PLAYER_ACTIVITY)
        return Gson().fromJson(json, Track::class.java)
    }


    private fun setTrackData(track: Track) {
        binding.apply {
            textViewTitle.text = track.trackName
            textViewArtist.text = track.artistName
            textViewPrimaryGenreNameValue.text = track.primaryGenreName
            textViewCountryValue.text = track.country
            setValueToTextView(textViewDurationValue, groupDuration, track.trackDuration)
            setValueToTextView(textViewCollectionNameValue, groupCollectionName, track.collectionName)
            setValueToTextView(textViewReleaseDateValue, groupReleaseDate, track.releaseDate)

            Glide.with(applicationContext)
                .load(Converter.getCoverArtwork(track.artworkUrl))
                .placeholder(R.drawable.img_placeholder)
                .centerInside()
                .transform(RoundedCorners(Converter.dpToPx(8f, applicationContext)))
                .into(imgViewTrackImage)
        }
    }

    private fun setValueToTextView(textView: TextView, group: Group, value: String?) {
        group.isVisible = !value.isNullOrEmpty()
        textView.text = value
    }

    private fun enableButton(isEnabled: Boolean) {
        binding.btnPlayPause.isEnabled = isEnabled
    }

    private fun changeButtonImg(isPlaying: Boolean) {
        binding.btnPlayPause.apply {
            if (isPlaying) {
                setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.ic_pause))
            } else {
                setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.ic_play))
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel?.onPause()
    }
}