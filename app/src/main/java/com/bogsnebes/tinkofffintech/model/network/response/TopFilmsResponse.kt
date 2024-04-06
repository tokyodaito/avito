package com.bogsnebes.tinkofffintech.model.network.response

import com.google.gson.annotations.SerializedName

data class TopFilmsResponse(
    @SerializedName("pages")
    val pagesCount: Int,
    @SerializedName("docs")
    val films: List<Film>
)

data class Film(
    @SerializedName("id")
    val filmId: Int,
    @SerializedName("name")
    val nameRu: String?,
    val nameEn: String?,
    val year: String,
    val ageRating: Int? = null,
    val genres: List<Genre>,
    @SerializedName("poster")
    val posterUrlPreview: Poster,
)

data class Poster(
    val url: String?,
    val previewUrl: String?
)

data class Genre(
    @SerializedName("name")
    val genre: String
)
