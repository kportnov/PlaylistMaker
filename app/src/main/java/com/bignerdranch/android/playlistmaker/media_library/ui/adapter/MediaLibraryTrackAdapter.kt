package com.bignerdranch.android.playlistmaker.media_library.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.databinding.RecycleSearchItemBinding
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.util.Converter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class MediaLibraryTrackAdapter(val clickListener: MediaLibraryTrackAdapterClickListener):
    RecyclerView.Adapter<MediaLibraryTrackAdapter.MediaLibraryTrackViewHolder>() {

    var trackList = mutableListOf<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaLibraryTrackViewHolder =
        MediaLibraryTrackViewHolder.from(parent)

    override fun getItemCount(): Int {
        return trackList.size
    }

    override fun onBindViewHolder(holder: MediaLibraryTrackViewHolder, position: Int) {
        val track = trackList[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            clickListener.onTrackClick(track)
        }
        holder.itemView.setOnLongClickListener {
            clickListener.onLongTrackClick(track)
            true
        }
    }

    interface MediaLibraryTrackAdapterClickListener {
        fun onTrackClick(track: Track)
        fun onLongTrackClick(track: Track)
    }

    class MediaLibraryTrackViewHolder(private val binding: RecycleSearchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(track: Track) {
            binding.apply {
                trackName.text = track.trackName
                trackArtist.text = track.artistName
                if (track.trackDuration != null) {
                    trackDuration.text = Converter.longToMMSS(track.trackDuration.toLong())
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
            fun from(parent: ViewGroup): MediaLibraryTrackViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = RecycleSearchItemBinding.inflate(inflater, parent, false)
                return MediaLibraryTrackViewHolder(binding)
            }
        }
    }
}