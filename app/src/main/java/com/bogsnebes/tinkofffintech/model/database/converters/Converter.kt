package com.bogsnebes.tinkofffintech.model.database.converters

import androidx.room.TypeConverter
import com.bogsnebes.tinkofffintech.model.network.response.Country
import com.bogsnebes.tinkofffintech.model.network.response.Genre
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converter {
    @TypeConverter
    fun fromCountryList(countries: List<Country>?): String? {
        if (countries == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<Country>>() {}.type
        return gson.toJson(countries, type)
    }

    @TypeConverter
    fun toCountryList(countryString: String?): List<Country>? {
        if (countryString == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<Country>>() {}.type
        return gson.fromJson(countryString, type)
    }

    @TypeConverter
    fun fromGenreList(genres: List<Genre>?): String? {
        if (genres == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<Genre>>() {}.type
        return gson.toJson(genres, type)
    }

    @TypeConverter
    fun toGenreList(genreString: String?): List<Genre>? {
        if (genreString == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<Genre>>() {}.type
        return gson.fromJson(genreString, type)
    }
}
