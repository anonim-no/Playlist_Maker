package com.example.playlistmaker.common.models

data class PlayList(
    val playListId: Int,
    val name: String,
    val description: String,
    val image: String?,
    val tracksCount: Int,
)