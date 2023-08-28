package com.example.playlistmaker.medialibrary.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.medialibrary.data.db.favorites.dao.FavoritesTrackDao
import com.example.playlistmaker.medialibrary.data.db.favorites.entity.FavoritesTrackEntity
import com.example.playlistmaker.medialibrary.data.db.playlists.dao.PlayListsDao
import com.example.playlistmaker.medialibrary.data.db.playlists.entity.PlayListEntity
import com.example.playlistmaker.medialibrary.data.db.playlists.entity.TrackEntity

@Database(version = 4, entities = [FavoritesTrackEntity::class, TrackEntity::class, PlayListEntity::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritesTrackDao(): FavoritesTrackDao

    abstract fun playListsTrackDao(): PlayListsDao
}