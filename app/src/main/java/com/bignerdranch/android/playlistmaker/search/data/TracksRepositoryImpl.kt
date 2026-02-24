package com.bignerdranch.android.playlistmaker.search.data

import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.search.domain.api.TracksRepository
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.search.data.dto.TracksSearchRequest
import com.bignerdranch.android.playlistmaker.search.data.dto.TracksSearchResponse
import com.bignerdranch.android.playlistmaker.util.Converter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): Flow<Result<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
               emit(Result.failure(Throwable(R.string.connection_problems.toString())))
            }
            200 -> {
                with(response as TracksSearchResponse) {
                    val data = results.map {
                        Track(
                            it.trackName,
                            it.artistName,
                            Converter.longToMMSS(it.trackDuration?.toLong() ?: 0),
                            it.artworkUrl,
                            it.trackId,
                            it.collectionName,
                            Converter.dateToYear(it.releaseDate),
                            it.primaryGenreName,
                            it.country,
                            it.previewUrl
                        )
                    }
                    emit(Result.success(data))
                }
            }

            else -> {
                emit(Result.failure(Throwable(R.string.no_respond.toString())))
            }

        }
    }
}