package com.example.playlistmaker.search.ui

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TracksHistory(private val sharedPreferences: SharedPreferences) {

    fun add(track: Track) {
        val tracksHistory = get()
        tracksHistory.remove(track)
        tracksHistory.add(0,track)
        if (tracksHistory.size>10) tracksHistory.removeLast()
        save(tracksHistory)
    }

    fun get(): ArrayList<Track> {
        val json = sharedPreferences.getString(TRACKS_HISTORY, null) ?: return arrayListOf()
        return Gson().fromJson(json, object : TypeToken<ArrayList<Track>>() {}.type)
    }

    fun clear() {
        sharedPreferences.edit { remove(TRACKS_HISTORY) }
    }

    private fun save(tracksHistory: MutableList<Track>) {
        val json = Gson().toJson(tracksHistory)
        sharedPreferences.edit { putString(TRACKS_HISTORY, json) }
    }

    companion object {
        const val TRACKS_HISTORY = "tracks_history"
    }

}