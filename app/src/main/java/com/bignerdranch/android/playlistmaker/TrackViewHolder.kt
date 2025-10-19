package com.bignerdranch.android.playlistmaker

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson


class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val trackName = itemView.findViewById<TextView>(R.id.track_name)
    private val trackArtist = itemView.findViewById<TextView>(R.id.track_artist)
    private val trackDuration = itemView.findViewById<TextView>(R.id.track_duration)
    private val trackImage = itemView.findViewById<ImageView>(R.id.imgViewTrackImage)

    fun bind(track: Track) {
        trackName.text = track.trackName
        trackArtist.text = track.artistName
        if (track.trackDuration != null) {
            trackDuration.text = Converter.longToMMSS(track.trackDuration)
        }
        Glide.with(itemView)
            .load(track.artworkUrl)
            .placeholder(R.drawable.img_placeholder)
            .centerInside()
            .transform(RoundedCorners(Converter.dpToPx(2f, itemView.context)))
            .into(trackImage)
    }
}

class TrackAdapter(private val trackList: MutableList<Track>, private val context: Context): RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_search_card, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val searchHistory = SearchHistory(context.getSharedPreferences(SHARED_PREFERENCES_SEARCH, 0))

        holder.bind(trackList[position])
        holder.itemView.setOnClickListener {
            searchHistory.addToHistoryList(trackList[position])
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra(KEY_PLAYER_ACTIVITY, Gson().toJson(trackList[position]))
            context.startActivity(intent)
        }
    }
}