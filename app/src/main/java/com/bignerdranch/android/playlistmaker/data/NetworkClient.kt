package com.bignerdranch.android.playlistmaker.data

import com.bignerdranch.android.playlistmaker.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response
}