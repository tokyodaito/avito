package com.bogsnebes.tinkofffintech.model.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FilmResponseEntity(
    @PrimaryKey val kinopoiskId: Long,
    val nameRu: String?,
    val nameEn: String?,
    val nameOriginal: String?,
    val posterUrl: String?,
    val description: String?,
    val shortDescription: String?,
)