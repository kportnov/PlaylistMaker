package com.bignerdranch.android.playlistmaker.search.data.storage

import android.content.SharedPreferences
import com.bignerdranch.android.playlistmaker.search.data.StorageClient
import com.google.gson.Gson
import java.lang.reflect.Type

class PrefsStorageClient<T>(
    private val dataKey: String,
    private val type: Type,
    private val gson: Gson,
    private val prefs: SharedPreferences
) : StorageClient<T> {

    override fun storeData(data: T) {
        prefs.edit().putString(dataKey, gson.toJson(data, type)).apply()
    }

    override fun getData(): T? {
        val dataJson = prefs.getString(dataKey, null)
        if (dataJson == null) {
            return null
        } else {
            return gson.fromJson(dataJson, type)
        }
    }

}