package com.bignerdranch.android.playlistmaker.media_library.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bignerdranch.android.playlistmaker.media_library.data.db.converters.Converters
import com.bignerdranch.android.playlistmaker.media_library.data.db.dao.PlaylistDao
import com.bignerdranch.android.playlistmaker.media_library.data.db.dao.TrackDao
import com.bignerdranch.android.playlistmaker.media_library.data.db.dao.TrackInPlaylistDao
import com.bignerdranch.android.playlistmaker.media_library.data.db.entity.PlaylistEntity
import com.bignerdranch.android.playlistmaker.media_library.data.db.entity.TrackEntity
import com.bignerdranch.android.playlistmaker.media_library.data.db.entity.TrackInPlaylistEntity

@Database(version = 1, entities = [TrackEntity::class, PlaylistEntity::class, TrackInPlaylistEntity::class])
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase(){

    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao

    abstract fun trackInPlaylistDao(): TrackInPlaylistDao

}