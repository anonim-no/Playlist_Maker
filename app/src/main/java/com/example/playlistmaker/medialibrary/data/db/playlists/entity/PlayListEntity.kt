package com.example.playlistmaker.medialibrary.data.db.playlists.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "play_lists_table")
data class PlayListEntity(
    @PrimaryKey(autoGenerate = true)
    val playListId: Int?,
    val name: String,
    val description: String,
    val image: String?,
    val tracksCount: Int,
    val createdAt: Long
)
