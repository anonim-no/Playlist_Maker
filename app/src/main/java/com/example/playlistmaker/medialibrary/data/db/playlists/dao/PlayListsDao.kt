package com.example.playlistmaker.medialibrary.data.db.playlists.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.playlistmaker.medialibrary.data.db.playlists.entity.PlayListEntity
import com.example.playlistmaker.medialibrary.data.db.playlists.entity.PlayListWithCountTracks
import com.example.playlistmaker.medialibrary.data.db.playlists.entity.PlayListsTrackEntity
import com.example.playlistmaker.medialibrary.data.db.playlists.entity.TrackPlayListEntity

@Dao
interface PlayListsDao {

    // добавляет плейлист
    @Insert(entity = PlayListEntity::class)
    suspend fun addPlayList(playList: PlayListEntity)

    @Insert(entity = PlayListsTrackEntity::class, onConflict = OnConflictStrategy.IGNORE) // добавляет трек
    suspend fun addPlayListsTrack(playListsTrackEntity: PlayListsTrackEntity)
    @Insert(entity = TrackPlayListEntity::class) // добавляем запись в промежуточную таблицу
    suspend fun addTrackPlayList(trackPlayListEntity: TrackPlayListEntity)
    @Transaction // в транзакци  добавляем трек и запись в промежуточную таблицу
    suspend fun addTrackToPlayList(
        playListsTrackEntity: PlayListsTrackEntity,
        trackPlayListEntity: TrackPlayListEntity
    ) {
        addPlayListsTrack(playListsTrackEntity)
        addTrackPlayList(trackPlayListEntity)
    }

    // получает список плейлистов с колв-ом треков
    @Query("SELECT playListId, name, description, image, (SELECT COUNT(id) FROM play_lists_track_table WHERE play_lists_track_table.playListId=play_lists_table.playListId) as tracksCount FROM play_lists_table ORDER BY playListId DESC")
    suspend fun getPlayLists(): List<PlayListWithCountTracks>

    // получает список треков плейлиста
    @Query("SELECT track_play_lists_table.* FROM track_play_lists_table LEFT JOIN play_lists_track_table ON track_play_lists_table.trackId=play_lists_track_table.trackId WHERE play_lists_track_table.playListId = :playListId  ORDER BY play_lists_track_table.id DESC")
    suspend fun getPlayListTracks(playListId: Int): List<PlayListsTrackEntity>

    // проверяет есть ли трек в плейлисте
    @Query("SELECT EXISTS (SELECT 1 FROM play_lists_track_table  WHERE trackId = :trackId AND playListId = :playListId)")
    suspend fun isTrackInPlayList(trackId: Int, playListId: Int): Boolean

    @Query("DELETE FROM play_lists_track_table WHERE playListId = :playListId AND trackId = :trackId")
    suspend fun deleteTrackFromTrackPlayList(playListId: Int, trackId: Int)
    @Query("DELETE FROM track_play_lists_table WHERE trackId NOT IN (SELECT DISTINCT(:trackId) FROM play_lists_track_table)")
    suspend fun deleteTrack(trackId: Int)
    @Transaction
    suspend fun deleteTrackFromPlayList(
        trackId: Int,
        playListId: Int
    ) {
        deleteTrackFromTrackPlayList(playListId, trackId)
        deleteTrack(trackId)
    }

}