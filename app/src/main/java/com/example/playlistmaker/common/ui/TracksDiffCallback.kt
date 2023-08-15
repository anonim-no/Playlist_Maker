package com.example.playlistmaker.common.ui

import androidx.recyclerview.widget.DiffUtil
import com.example.playlistmaker.common.models.Track

class TracksDiffCallback(
    private val oldTrackList: List<Track>,
    private val newTrackList: List<Track>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldTrackList.size
    }

    override fun getNewListSize(): Int {
        return newTrackList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldTrack = oldTrackList[oldItemPosition]
        val newTrack = newTrackList[newItemPosition]
        return oldTrack.trackId == newTrack.trackId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldTrack = oldTrackList[oldItemPosition]
        val newTrack = newTrackList[newItemPosition]
        return oldTrack == newTrack
    }

}