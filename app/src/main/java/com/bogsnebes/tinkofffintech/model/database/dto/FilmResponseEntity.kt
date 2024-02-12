package com.bogsnebes.tinkofffintech.model.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bogsnebes.tinkofffintech.model.network.response.Country
import com.bogsnebes.tinkofffintech.model.network.response.Genre

@Entity
data class FilmResponseEntity(
    @PrimaryKey val kinopoiskId: Long,
    val nameRu: String?,
    val nameEn: String?,
    val nameOriginal: String?,
    val posterUrl: String?,
    val description: String?,
    val shortDescription: String?,
    val genres: List<Genre>,
    val countries: List<Country>
)