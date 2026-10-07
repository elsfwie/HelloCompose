package com.example.ch07.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromTags(tags: List<String>): String =
        gson.toJson(tags)

    @TypeConverter
    fun toTags(json: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type

        return gson.fromJson<List<String>>(json, type) ?: emptyList()
    }
}