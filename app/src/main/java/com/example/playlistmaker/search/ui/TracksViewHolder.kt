package com.example.playlistmaker.search.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.*

class TracksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val trackName: TextView = itemView.findViewById(R.id.tvTrackName)
    private val artistName: TextView = itemView.findViewById(R.id.tvArtistName)
    private val trackTime: TextView = itemView.findViewById(R.id.tvTrackTime)
    private val artworkUrl100: ImageView = itemView.findViewById(R.id.ivTrackArt)

    fun bind(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        Glide
            .with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder)
            .centerCrop()
            .transform(
                RoundedCorners(
                    itemView.resources.getDimensionPixelSize(
                        R.dimen.track_list_album_corner_radius
                    )
                )
            )
            .into(artworkUrl100)
    }
}