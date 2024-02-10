package com.bogsnebes.tinkofffintech.model.network.response

data class TopFilmsResponse(
    val pagesCount: Int,
    val films: List<Film>
)

data class Film(
    val filmId: Int,
    val nameRu: String?,
    val nameEn: String?,
    val year: String,
    val genres: List<Genre>,
    val posterUrlPreview: String,
)

data class Genre(
    val genre: String
)
