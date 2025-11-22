package com.bignerdranch.android.playlistmaker.data

import com.bignerdranch.android.playlistmaker.Converter
import com.bignerdranch.android.playlistmaker.data.dto.TracksSearchRequest
import com.bignerdranch.android.playlistmaker.data.dto.TracksSearchResponse
import com.bignerdranch.android.playlistmaker.domain.api.TracksRepository
import com.bignerdranch.android.playlistmaker.domain.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        if (response.resultCode == 200) {
            return (response as TracksSearchResponse).results.map {
                Track(
                    it.trackName,
                    it.artistName,
                    Converter.longToMMSS(it.trackDuration),
                    it.artworkUrl,
                    it.trackId,
                    it.collectionName,
                    Converter.dateToYear(it.releaseDate),
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl
                )
            }
        } else {
            return emptyList()
        }
    }
}