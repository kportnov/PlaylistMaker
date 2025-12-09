package com.bignerdranch.android.playlistmaker.search.data

import com.bignerdranch.android.playlistmaker.search.data.dto.TrackHistoryDto
import com.bignerdranch.android.playlistmaker.search.domain.api.TracksHistoryRepository
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.util.Resource


private const val MAXIMUM_SEARCH_HISTORY_ITEMS = 10
class TracksHistoryRepositoryImpl(
    private val storage: StorageClient<ArrayList<TrackHistoryDto>>): TracksHistoryRepository {

    override fun addToHistory(track: Track) {
        val tracks = getHistory().data?.toMutableList()

        if (tracks?.map { it.trackId }!!.contains(track.trackId)) {
            tracks.remove(tracks.find { it.trackId == track.trackId })
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
                it.trackId,
                it.collectionName,
                it.releaseDate,
                it.primaryGenreName,
                it.country,
                it.previewUrl
            )
        }
        storage.storeData(ArrayList(dto))
    }

    override fun getHistory(): Resource<List<Track>> {
        val tracks = storage.getData()?.map {
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
                it.previewUrl
            )
        } ?: listOf()

        return Resource.Success(tracks)
    }

    override fun clearHistory() {
        storage.storeData(arrayListOf())
    }
}