package com.bogsnebes.tinkofffintech.model.network

data class FilmResponse(
    val pagesCount: Int,
    val films: List<Film>
)

data class Film(
    val filmId: Long,
    val nameRu: String?,
    val nameEn: String?,
    val year: String,
    val filmLength: String?,
    val countries: List<Country>,
    val genres: List<Genre>,
    val rating: String,
    val ratingVoteCount: Int,
    val posterUrl: String,
    val posterUrlPreview: String,
    val ratingChange: String?,
    val isRatingUp: Boolean?,
    val isAfisha: Int
)

data class Country(
    val country: String
)

data class Genre(
    val genre: String
)
