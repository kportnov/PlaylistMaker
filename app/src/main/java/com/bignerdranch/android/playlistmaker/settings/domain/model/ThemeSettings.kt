package com.bignerdranch.android.playlistmaker.settings.domain.model

enum class ThemeSettings {
    DAY_MODE,
    NIGHT_MODE;

    fun isNight(): Boolean {
        return this != DAY_MODE
    }
}