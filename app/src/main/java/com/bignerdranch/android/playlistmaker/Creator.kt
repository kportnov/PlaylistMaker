package com.bignerdranch.android.playlistmaker

import android.content.Context
import com.bignerdranch.android.playlistmaker.data.TrackHistoryManager
import com.bignerdranch.android.playlistmaker.data.TracksRepositoryImpl
import com.bignerdranch.android.playlistmaker.data.network.RetrofitNetworkClient
import com.bignerdranch.android.playlistmaker.domain.api.TracksHistoryInteractor
import com.bignerdranch.android.playlistmaker.domain.api.TracksInteractor
import com.bignerdranch.android.playlistmaker.domain.api.TracksRepository
import com.bignerdranch.android.playlistmaker.domain.impl.TracksHistoryInteractorImpl
import com.bignerdranch.android.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    fun getTrackHistoryManager(context: Context): TrackHistoryManager {
        return TrackHistoryManager(context)
    }

    fun getTracksHistoryInteractor(trackHistoryManager: TrackHistoryManager): TracksHistoryInteractor {
        return TracksHistoryInteractorImpl(trackHistoryManager)
    }

}
