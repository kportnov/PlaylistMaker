package com.bignerdranch.android.playlistmaker.di

import android.content.Context
import com.bignerdranch.android.playlistmaker.search.data.NetworkClient
import com.bignerdranch.android.playlistmaker.search.data.StorageClient
import com.bignerdranch.android.playlistmaker.search.data.dto.TrackHistoryDto
import com.bignerdranch.android.playlistmaker.search.data.network.ITunesApiService
import com.bignerdranch.android.playlistmaker.search.data.network.RetrofitNetworkClient
import com.bignerdranch.android.playlistmaker.search.data.storage.PrefsStorageClient
import com.bignerdranch.android.playlistmaker.settings.data.ExternalNavigator
import com.bignerdranch.android.playlistmaker.settings.domain.model.ThemeSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors

private const val ITUNES_SEARCH_BASE_URL = "https://itunes.apple.com"
private const val THEME_KEY = "THEME"
private const val SEARCH_HISTORY_KEY = "HISTORY"

val dataModule = module {

    //API
    single<ITunesApiService> {
        Retrofit.Builder()
            .baseUrl(ITUNES_SEARCH_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApiService::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }

    //StorageHistory
    factory { Gson() }

    factory { Executors.newCachedThreadPool() }

    single {
        androidContext()
            .getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE)
    }

    single<StorageClient<ArrayList<TrackHistoryDto>>>(named("STORAGE_HISTORY")) {
        PrefsStorageClient(
            SEARCH_HISTORY_KEY,
            object : TypeToken<ArrayList<TrackHistoryDto>>() {}.type,
            get(),
            get()
        )
    }

    //StorageThemeSettings
    single<StorageClient<ThemeSettings>>(named("STORAGE_THEME")) {
        PrefsStorageClient(
        THEME_KEY,
        object : TypeToken<ThemeSettings>() {}.type,
        get(),
        get()
        )
    }

    //Settings ExternalNavigator
    single {
        ExternalNavigator(androidContext())
    }
}