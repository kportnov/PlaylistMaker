package com.bignerdranch.android.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.playlistmaker.util.Converter
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.databinding.RecycleSearchCardBinding
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


class TrackAdapter(val clickListener: TrackAdapterClickListener): RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    var trackList = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
        TrackViewHolder.from(parent)

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

    class TrackViewHolder(private val binding: RecycleSearchCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(track: Track) {
            binding.apply {
                trackName.text = track.trackName
                trackArtist.text = track.artistName
                if (track.trackDuration != null) {
                    trackDuration.text = track.trackDuration
                }
            }

            Glide.with(itemView)
                .load(track.artworkUrl)
                .placeholder(R.drawable.img_placeholder)
                .centerInside()
                .transform(RoundedCorners(Converter.dpToPx(2f, itemView.context)))
                .into(binding.imgViewTrackImage)
        }

        companion object {
            fun from(parent: ViewGroup): TrackViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = RecycleSearchCardBinding.inflate(inflater, parent, false)
                return TrackViewHolder(binding)
            }
        }
    }
}