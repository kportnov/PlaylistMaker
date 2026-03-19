package com.bignerdranch.android.playlistmaker.media_library.data.db.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@ProvidedTypeConverter
class Converters(private val gson: Gson) {

    @TypeConverter
    fun fromList(list: List<Int>?): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toList(data: String?): List<Int> {
        if (data.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(data, type)
    }
}