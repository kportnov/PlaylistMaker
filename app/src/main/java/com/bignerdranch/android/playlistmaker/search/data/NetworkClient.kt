package com.bignerdranch.android.playlistmaker.search.data

import com.bignerdranch.android.playlistmaker.search.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response
}