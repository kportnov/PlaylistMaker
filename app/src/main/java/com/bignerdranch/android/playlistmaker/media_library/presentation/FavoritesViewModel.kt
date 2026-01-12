package com.bignerdranch.android.playlistmaker.media_library.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoritesViewModel(): ViewModel() {
    private val favoritesLiveData = MutableLiveData<String>()
    fun observeLiveData(): LiveData<String> = favoritesLiveData

}