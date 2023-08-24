package com.example.playlistmaker.medialibrary.domain.models

data class PlayList(
    val playListId: Int,
    val name: String,
    val description: String,
    val image: String?,
    val tracksCount: Int,
)