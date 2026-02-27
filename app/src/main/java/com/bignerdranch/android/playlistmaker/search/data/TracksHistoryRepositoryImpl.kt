package com.bignerdranch.android.playlistmaker.search.data

import com.bignerdranch.android.playlistmaker.media_library.data.db.AppDatabase
import com.bignerdranch.android.playlistmaker.search.data.dto.TrackHistoryDto
import com.bignerdranch.android.playlistmaker.search.domain.api.TracksHistoryRepository
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private const val MAXIMUM_SEARCH_HISTORY_ITEMS = 10
class TracksHistoryRepositoryImpl(
    private val storage: StorageClient<ArrayList<TrackHistoryDto>>,
    private val appDatabase: AppDatabase
    ): TracksHistoryRepository {

    override fun addToHistory(track: Track) {

        val tracks = getHistoryList().toMutableList()

        if (tracks.map { it.id }.contains(track.id)) {
            tracks.remove(tracks.find { it.id == track.id })
            tracks.add(0,track)
        } else {
            tracks.add(0,track)
        }
        if (tracks.size > MAXIMUM_SEARCH_HISTORY_ITEMS) {
            tracks.removeAt(tracks.size - 1)
        }
        saveHistory(tracks)
    }

    override fun saveHistory(tracksHistory: List<Track>) {
        val dto = tracksHistory.map {
            TrackHistoryDto(
                it.trackName,
                it.artistName,
                it.trackDuration,
                it.artworkUrl,
                it.id,
                it.collectionName,
                it.releaseDate,
                it.primaryGenreName,
                it.country,
                it.previewUrl
            )
        }
        storage.storeData(ArrayList(dto))
    }

    override fun getHistory(): Flow<List<Track>> = flow {
        val favoritesIds = appDatabase.trackDao().getTracksId()
        val data = getHistoryList()
        data.forEach {
            it.isFavorite = favoritesIds.contains(it.id)
        }
        emit(data)
    }

    override fun clearHistory() {
        storage.storeData(arrayListOf())
    }

    private fun getHistoryList(): List<Track> {
        return storage.getData()?.map {
            Track(
                it.trackName,
                it.artistName,
                it.trackDuration,
                it.artworkUrl,
                it.trackId,
                it.collectionName,
                it.releaseDate,
                it.primaryGenreName,
                it.country,
                it.previewUrl)
        } ?: listOf()
    }
}