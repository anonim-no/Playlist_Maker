package com.example.playlistmaker.common.models

import java.io.Serializable

data class PlayList(
    val playListId: Int,
    val name: String,
    val description: String,
    val image: String?,
    val tracksCount: Int,
) : Serializable {

    override fun equals(other: Any?): Boolean {
        return if (other !is PlayList) {
            false
        } else {
            other.playListId == playListId
        }
    }

    override fun hashCode(): Int {
        return playListId
    }
}