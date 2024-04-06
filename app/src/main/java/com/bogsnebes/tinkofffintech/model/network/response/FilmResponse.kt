package com.bogsnebes.tinkofffintech.model.network.response

import com.google.gson.annotations.SerializedName

data class FilmResponse(
    val kinopoiskId: Long?,
    val nameRu: String?,
    val nameEn: String?,
    val nameOriginal: String?,
    @SerializedName("poster")
    val posterUrl: Poster?,
    val description: String?,
    val shortDescription: String?,
    val countries: List<Country>?,
    val genres: List<Genre>?,
)

data class Country(
    val country: String
)
