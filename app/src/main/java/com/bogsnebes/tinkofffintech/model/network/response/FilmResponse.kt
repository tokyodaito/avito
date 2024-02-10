package com.bogsnebes.tinkofffintech.model.network.response

data class FilmResponse(
    val kinopoiskId: Long?,
    val nameRu: String?,
    val nameEn: String?,
    val nameOriginal: String?,
    val posterUrl: String?,
    val description: String?,
    val shortDescription: String?,
    val countries: List<Country>?,
    val genres: List<Genre>?,
)
