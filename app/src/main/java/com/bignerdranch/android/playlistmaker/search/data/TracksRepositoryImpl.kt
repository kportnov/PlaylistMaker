package com.bignerdranch.android.playlistmaker.search.data

import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.search.domain.api.TracksRepository
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.search.data.dto.TracksSearchRequest
import com.bignerdranch.android.playlistmaker.search.data.dto.TracksSearchResponse
import com.bignerdranch.android.playlistmaker.util.Converter
import com.bignerdranch.android.playlistmaker.util.Resource

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error(R.string.connection_problems.toString())
            }
            200 -> {
                val response = response as TracksSearchResponse

                Resource.Success(response.results.map {
                    Track(
                        it.trackName,
                        it.artistName,
                        Converter.longToMMSS(it.trackDuration?.toLong()),
                        it.artworkUrl,
                        it.trackId,
                        it.collectionName,
                        Converter.dateToYear(it.releaseDate),
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl
                    )
                })
            }

            else -> {
                Resource.Error(R.string.no_respond.toString())
            }

        }
    }
}