package com.bignerdranch.android.playlistmaker.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.playlistmaker.util.Converter
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.domain.models.Track
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


class TrackAdapter(val clickListener: TrackAdapterClickListener): RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    var trackList = emptyList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder = TrackViewHolder(parent)

    override fun getItemCount(): Int {
        return trackList.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener { clickListener.onTrackClick(trackList[position]) }
    }

    fun interface TrackAdapterClickListener {
        fun onTrackClick(track: Track)
    }

    class TrackViewHolder(parent: ViewGroup): RecyclerView.ViewHolder(LayoutInflater.from(parent.context)
        .inflate(R.layout.recycle_search_card, parent, false)) {

        private val trackName = itemView.findViewById<TextView>(R.id.track_name)
        private val trackArtist = itemView.findViewById<TextView>(R.id.track_artist)
        private val trackDuration = itemView.findViewById<TextView>(R.id.track_duration)
        private val trackImage = itemView.findViewById<ImageView>(R.id.imgViewTrackImage)

        fun bind(track: Track) {
            trackName.text = track.trackName
            trackArtist.text = track.artistName
            if (track.trackDuration != null) {
                trackDuration.text = track.trackDuration
            }
            Glide.with(itemView)
                .load(track.artworkUrl)
                .placeholder(R.drawable.img_placeholder)
                .centerInside()
                .transform(RoundedCorners(Converter.dpToPx(2f, itemView.context)))
                .into(trackImage)
        }
    }

}