package com.bignerdranch.android.playlistmaker.media_library.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bignerdranch.android.playlistmaker.media_library.data.db.entity.TrackInPlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackInPlaylistDao {

    @Insert(entity = TrackInPlaylistEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: TrackInPlaylistEntity)

    @Delete(entity = TrackInPlaylistEntity::class)
    suspend fun deleteTrack(track: TrackInPlaylistEntity)

    @Query("SELECT * FROM playlistTracks")
    fun getTracks(): Flow<List<TrackInPlaylistEntity>>

    @Query("SELECT trackId FROM playlistTracks")
    suspend fun getTracksId(): List<String>
}
