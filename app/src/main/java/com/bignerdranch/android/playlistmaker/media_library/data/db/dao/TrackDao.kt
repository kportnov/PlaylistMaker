package com.bignerdranch.android.playlistmaker.media_library.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bignerdranch.android.playlistmaker.media_library.data.db.entity.TrackEntity


@Dao
interface TrackDao {

    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Delete(entity = TrackEntity::class)
    suspend fun deleteTrack(track: TrackEntity)

    @Query("SELECT * FROM favorites")
    suspend fun getTracks(): List<TrackEntity>

    @Query("SELECT trackId FROM favorites")
    suspend fun getTracksId(): List<String>
}
