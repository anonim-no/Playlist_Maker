package com.example.playlistmaker.search.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.search.data.LocalStorage
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesClient(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : LocalStorage {

    override fun addTracksHistory(track: Track) {
        val tracksHistory = getTracksHistory().toMutableList()
        tracksHistory.remove(track)
        tracksHistory.add(0, track)
        if (tracksHistory.size > TRACKS_HISTORY_MAX) tracksHistory.removeLast()
        val json = gson.toJson(tracksHistory)
        sharedPreferences.edit { putString(TRACKS_HISTORY, json) }
    }

    override fun clearTracksHistory() {
        sharedPreferences.edit { remove(TRACKS_HISTORY) }
    }

    override fun getTracksHistory(): List<Track> {
        val json = sharedPreferences.getString(TRACKS_HISTORY, null) ?: return listOf()
        return gson.fromJson(json, object : TypeToken<List<Track>>() {}.type)
    }

    companion object {
        const val TRACKS_HISTORY = "tracks_history"
        const val TRACKS_HISTORY_MAX = 10
    }

}