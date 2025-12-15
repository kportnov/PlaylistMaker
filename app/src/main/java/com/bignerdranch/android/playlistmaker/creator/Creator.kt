package com.bignerdranch.android.playlistmaker.creator

import android.app.Application

object Creator {
    private lateinit var application: Application

    fun initApplication(application: Application) {
        this.application = application
    }
}