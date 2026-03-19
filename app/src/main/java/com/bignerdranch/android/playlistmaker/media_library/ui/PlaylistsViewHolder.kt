package com.bignerdranch.android.playlistmaker.media_library.ui

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

class PlaylistsViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val image: ImageView = itemView.findViewById(R.id.imgViewTrackImage)
    private val title: TextView = itemView.findViewById(R.id.textViewPlaylistName)
    private val number: TextView = itemView.findViewById(R.id.textViewTracksNumber)

    fun bind(playlist: Playlist) {
        val file = File(playlist.imagePath ?: "")
        Glide.with(itemView)
            .load(Uri.fromFile(file))
            .placeholder(R.drawable.img_placeholder)
            .transform(CenterCrop(), RoundedCorners(Converter.dpToPx(8f, itemView.context)))
            .into(image)

        val tracksText = itemView.context.resources.getQuantityString(
            R.plurals.tracks_count,
            playlist.tracksNumber,
            playlist.tracksNumber)

        title.text = playlist.playlistName
        number.text = tracksText
    }
}

class PlaylistsAdapter(private val playlists: List<Playlist>): RecyclerView.Adapter<PlaylistsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_item_card, parent, false)
        return PlaylistsViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PlaylistsViewHolder,
        position: Int
    ) {
       holder.bind(playlists[position])
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

}