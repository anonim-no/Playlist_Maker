package com.example.playlistmaker.medialibrary.data.db.playlists.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_play_lists_table")
data class PlayListsTrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = -1,
    val trackId: Int,
    val trackName: String, // Название композиции
    val artistName: String?, // Имя исполнителя
    val trackTimeMillis: Long?, // Продолжительность трека
    val artworkUrl100: String?, // Ссылка на изображение обложки
    val collectionName: String?, // Название альбома
    val releaseDate: String?, // Год релиза трека
    val primaryGenreName: String?, // Жанр трека
    val country: String?, // Страна исполнителя
    val previewUrl: String?,
    val createdAt: Long, // дата создания записи для сортировки
    val playListId: Int
)