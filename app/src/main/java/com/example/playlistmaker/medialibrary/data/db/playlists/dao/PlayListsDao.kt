package com.example.playlistmaker.medialibrary.data.db.playlists.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.playlistmaker.medialibrary.data.db.playlists.entity.PlayListEntity
import com.example.playlistmaker.medialibrary.data.db.playlists.entity.PlayListsTrackEntity

@Dao
interface PlayListsDao {
    @Insert(entity = PlayListEntity::class)
    suspend fun addPlayList(playList: PlayListEntity)

    @Insert(entity = PlayListsTrackEntity::class)
    suspend fun addTrackToPlayList(track: PlayListsTrackEntity)

    @Query("SELECT playListId, name, description, image, (SELECT COUNT(trackId) FROM track_play_lists_table WHERE playListId=play_lists_table.playListId) as tracksCount, createdAt FROM play_lists_table ORDER BY createdAt DESC")
    suspend fun getPlayLists(): List<PlayListEntity>

    @Query("SELECT * FROM track_play_lists_table WHERE playListId = :playListId")
    suspend fun getPlayListTracks(playListId: Int): List<PlayListsTrackEntity>

}