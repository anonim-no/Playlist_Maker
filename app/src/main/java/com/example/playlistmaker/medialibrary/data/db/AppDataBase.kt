package com.example.playlistmaker.medialibrary.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.medialibrary.data.db.favorites.dao.FavoritesTrackDao
import com.example.playlistmaker.medialibrary.data.db.favorites.entity.FavoritesTrackEntity
import com.example.playlistmaker.medialibrary.data.db.playlists.dao.PlayListsDao
import com.example.playlistmaker.medialibrary.data.db.playlists.entity.PlayListEntity
import com.example.playlistmaker.medialibrary.data.db.playlists.entity.PlayListsTrackEntity
import com.example.playlistmaker.medialibrary.data.db.playlists.entity.TrackPlayListEntity

@Database(version = 7, entities = [FavoritesTrackEntity::class, PlayListsTrackEntity::class, PlayListEntity::class, TrackPlayListEntity::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritesTrackDao(): FavoritesTrackDao

    abstract fun playListsTrackDao(): PlayListsDao
}