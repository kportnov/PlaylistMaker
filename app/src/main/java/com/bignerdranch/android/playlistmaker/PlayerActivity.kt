package com.bignerdranch.android.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson

const val KEY_PLAYER_ACTIVITY = "key player activity"

class PlayerActivity : AppCompatActivity() {

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
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.constraint_activity_player)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val buttonBack = findViewById<ImageButton>(R.id.button_back)

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

        buttonBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        setTrack()
    }



    private fun setTrack() {
        val track = getTrack()

        trackName.text = track.trackName
        setImage(track)
        artistName.text = track.artistName
        genreName.text = track.primaryGenreName
        country.text = track.country

        setValueToTextView(trackDuration, groupTrackDuration, Converter.longToMMSS(track.trackDuration))
        setValueToTextView(collectionName, groupCollectionName, track.collectionName)
        setValueToTextView(releaseDate, groupReleaseDate, Converter.dateToYear(track.releaseDate))
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
        if (value.isNullOrEmpty()) {
            group.visibility = View.GONE
        } else {
            textView.text = value
        }
    }
}