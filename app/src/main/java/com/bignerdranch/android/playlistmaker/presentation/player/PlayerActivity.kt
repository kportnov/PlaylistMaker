package com.bignerdranch.android.playlistmaker.presentation.player

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bignerdranch.android.playlistmaker.util.Converter
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.domain.models.Track
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson

const val KEY_PLAYER_ACTIVITY = "key player activity"

class PlayerActivity : AppCompatActivity() {


    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private var mainThreadHandler: Handler? = null
    private var timeManager: Runnable? = null


    private lateinit var btnPlay: ImageButton

    private lateinit var trackName: TextView
    private lateinit var trackImage: ImageView
    private lateinit var artistName: TextView
    private lateinit var currentTime: TextView
    private lateinit var trackDuration: TextView
    private lateinit var collectionName: TextView
    private lateinit var releaseDate: TextView
    private lateinit var genreName: TextView
    private lateinit var country: TextView

    private lateinit var groupTrackDuration: Group
    private lateinit var groupCollectionName: Group
    private lateinit var groupReleaseDate: Group
    private lateinit var groupGenreName: Group
    private lateinit var groupCountry: Group

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.constraint_activity_player)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val buttonBack = findViewById<ImageButton>(R.id.button_back)
        btnPlay = findViewById(R.id.btnPlayPause)

        mainThreadHandler = Handler(Looper.getMainLooper())
        timeManager = manageTime()

        groupTrackDuration = findViewById(R.id.groupDuration)
        groupCollectionName = findViewById(R.id.groupCollectionName)
        groupReleaseDate = findViewById(R.id.groupReleaseDate)
        groupGenreName = findViewById(R.id.groupPrimaryGenreName)
        groupCountry = findViewById(R.id.groupCountry)


        trackName = findViewById(R.id.textViewTitle)
        trackImage = findViewById(R.id.imgViewTrackImage)
        artistName = findViewById(R.id.textViewArtist)
        currentTime = findViewById(R.id.textViewCurrentTime)
        trackDuration = findViewById(R.id.textViewDurationValue)
        collectionName = findViewById(R.id.textViewCollectionNameValue)
        releaseDate = findViewById(R.id.textViewReleaseDateValue)
        genreName = findViewById(R.id.textViewPrimaryGenreNameValue)
        country = findViewById(R.id.textViewCountryValue)

        setTrack()

        buttonBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        btnPlay.setOnClickListener { playbackControl() }
    }



    private fun setTrack() {
        val track = getTrack()

        trackName.text = track.trackName
        setImage(track)
        artistName.text = track.artistName
        genreName.text = track.primaryGenreName
        country.text = track.country

        setValueToTextView(trackDuration, groupTrackDuration, track.trackDuration)
        setValueToTextView(collectionName, groupCollectionName, track.collectionName)
        setValueToTextView(releaseDate, groupReleaseDate, track.releaseDate)

        preparePlayer(track.previewUrl)
    }

    private fun getTrack(): Track {
        val json = intent.getStringExtra(KEY_PLAYER_ACTIVITY)
        return Gson().fromJson(json, Track::class.java)
    }

    private fun setImage(track: Track) {
        val artworkUrl = Converter.getCoverArtwork(track.artworkUrl)
        Glide.with(trackImage)
            .load(artworkUrl)
            .placeholder(R.drawable.img_placeholder)
            .centerInside()
            .transform(RoundedCorners(Converter.dpToPx(8f, this)))
            .into(trackImage)
    }

    private fun setValueToTextView(textView: TextView, group: Group, value: String?) {
        group.isVisible = !value.isNullOrEmpty()
        textView.text = value
    }

    private fun preparePlayer(url: String?) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            btnPlay.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            btnPlay.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.ic_play))
            playerState = STATE_PREPARED
            mainThreadHandler?.removeCallbacks(timeManager!!)
            currentTime.text = getString(R.string._0_00)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        btnPlay.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.ic_pause))
        mainThreadHandler?.post(timeManager!!)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        btnPlay.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.ic_play))
        playerState = STATE_PAUSED
        mainThreadHandler?.removeCallbacks(timeManager!!)
    }

    private fun playbackControl() {

        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun manageTime() : Runnable {
        return object : Runnable {
            override fun run() {
                when (playerState) {
                    STATE_PLAYING -> {
                        val currentPosition = mediaPlayer.currentPosition
                        currentTime.text = Converter.longToMMSS(currentPosition.toString())
                    }
                }
                mainThreadHandler?.postDelayed(this, REFRESH_TIME_DELAY_MILLIS)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler?.removeCallbacks(timeManager!!)
        mediaPlayer.release()
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val REFRESH_TIME_DELAY_MILLIS = 300L

    }
}