package com.example.playlistmaker.search

import android.content.SharedPreferences
import com.example.playlistmaker.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TracksHistory(private val sharedPreferences: SharedPreferences) {

    companion object {
        const val TRACKS_HISTORY = "tracks_history"
    }

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
        val tracksHistory = ArrayList<Track>()
        save(tracksHistory)
    }

    private fun save(tracksHistory: MutableList<Track>) {
        val json = Gson().toJson(tracksHistory)
        sharedPreferences.edit()
            .putString(TRACKS_HISTORY, json)
            .apply()
    }
}