package com.bignerdranch.android.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import androidx.room.Room
import com.bignerdranch.android.playlistmaker.media_library.data.analytics.Analytics
import com.bignerdranch.android.playlistmaker.media_library.data.analytics.FirebaseAnalyticsImpl
import com.bignerdranch.android.playlistmaker.media_library.data.db.AppDatabase
import com.bignerdranch.android.playlistmaker.media_library.data.db.converters.Converters
import com.bignerdranch.android.playlistmaker.media_library.presentation.formatter.PlaylistShareFormatter
import com.bignerdranch.android.playlistmaker.media_library.presentation.navigator.ExternalNavigatorShare
import com.bignerdranch.android.playlistmaker.media_library.presentation.navigator.ExternalNavigatorShareImpl
import com.bignerdranch.android.playlistmaker.search.data.NetworkClient
import com.bignerdranch.android.playlistmaker.search.data.StorageClient
import com.bignerdranch.android.playlistmaker.search.data.dto.TrackHistoryDto
import com.bignerdranch.android.playlistmaker.search.data.network.ITunesApiService
import com.bignerdranch.android.playlistmaker.search.data.network.RetrofitNetworkClient
import com.bignerdranch.android.playlistmaker.search.data.storage.PrefsStorageClient
import com.bignerdranch.android.playlistmaker.settings.data.ExternalNavigator
import com.bignerdranch.android.playlistmaker.settings.domain.model.ThemeSettings
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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

    single {
        PlaylistShareFormatter(androidContext())
    }

    single<ExternalNavigatorShare> {
        ExternalNavigatorShareImpl(androidContext())
    }

    //Settings ExternalNavigator
    single {
        ExternalNavigator(androidContext())
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .addTypeConverter(Converters(get()))
            .build()
    }

    factory { MediaPlayer() }

    single {
        FirebaseAnalytics.getInstance(androidContext())
    }

    single<Analytics> {
        FirebaseAnalyticsImpl(get())
    }
}