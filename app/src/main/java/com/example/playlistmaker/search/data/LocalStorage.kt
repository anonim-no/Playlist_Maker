package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.models.Track

interface LocalStorage {
    fun addTracksHistory(track: Track)
    fun clearTracksHistory()
    fun getTracksHistory(): ArrayList<Track>
}