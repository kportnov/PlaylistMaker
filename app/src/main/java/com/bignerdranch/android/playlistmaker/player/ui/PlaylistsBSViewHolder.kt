package com.bignerdranch.android.playlistmaker.player.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.media_library.domain.models.Playlist
import com.bignerdranch.android.playlistmaker.util.Converter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.io.File

class PlaylistsBSViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val image: ImageView = itemView.findViewById(R.id.imgViewPlaylistImageLine)
    private val title: TextView = itemView.findViewById(R.id.textViewPlaylistNameLine)
    private val number: TextView = itemView.findViewById(R.id.textViewTracksNumberLine)

    fun bind(playlist: Playlist) {
        val file = File(playlist.imagePath ?: "")
        Glide.with(itemView)
            .load(Uri.fromFile(file))
            .placeholder(R.drawable.img_placeholder)
            .transform(CenterCrop(), RoundedCorners(Converter.dpToPx(4f, itemView.context)))
            .into(image)

        val tracksText = itemView.context.resources.getQuantityString(
            R.plurals.tracks_count,
            playlist.tracksNumber,
            playlist.tracksNumber)

        title.text = playlist.playlistName
        number.text = tracksText
    }
}

class PlaylistsBSAdapter(val clickListener: PlaylistAdapterClickListener): RecyclerView.Adapter<PlaylistsBSViewHolder>() {

    var playlists = ArrayList<Playlist>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistsBSViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_item, parent, false)
        return PlaylistsBSViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PlaylistsBSViewHolder,
        position: Int
    ) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { clickListener.onPlaylistClick(playlists[position]) }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
    fun interface PlaylistAdapterClickListener {
        fun onPlaylistClick(playlist: Playlist)
    }

}