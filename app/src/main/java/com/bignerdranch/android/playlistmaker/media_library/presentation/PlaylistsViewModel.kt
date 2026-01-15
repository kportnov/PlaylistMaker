package com.bignerdranch.android.playlistmaker.media_library.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaylistsViewModel(): ViewModel() {
    private val playlistsLiveData = MutableLiveData<String>()
    fun observeLiveData(): LiveData<String> = playlistsLiveData

}