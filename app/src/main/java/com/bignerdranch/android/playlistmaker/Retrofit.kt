package com.bignerdranch.android.playlistmaker

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


class TrackResponse(@SerializedName("results") val tracks: ArrayList<Track>)

interface ITunesSearchAPI {

    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<TrackResponse>
}

