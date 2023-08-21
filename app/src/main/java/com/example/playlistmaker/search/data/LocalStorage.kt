package com.example.playlistmaker.search.data

import com.example.playlistmaker.common.models.Track

interface LocalStorage {
    fun addTracksHistory(track: Track)
    fun clearTracksHistory()
    fun getTracksHistory(): List<Track>
}