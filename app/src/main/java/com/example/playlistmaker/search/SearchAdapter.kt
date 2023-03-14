package com.example.playlistmaker.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.models.Track

class SearchAdapter(private val clickListener: TrackClickListener) : RecyclerView.Adapter<SearchViewHolder>() {

    var tracks = ArrayList<Track>()
        set(newTrackList) {
            val diffResult = DiffUtil.calculateDiff(
                TracksDiffCallback(field, newTrackList)
            )
            field = newTrackList
            diffResult.dispatchUpdatesTo(this)
        }

    fun clearTracks() {
        tracks = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search, parent, false)
        return SearchViewHolder(view)
    }

    override fun getItemCount(): Int = tracks.size

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener { clickListener.onTrackClick(tracks.get(position)) }
    }

    fun interface TrackClickListener {
        fun onTrackClick(track: Track)
    }
}