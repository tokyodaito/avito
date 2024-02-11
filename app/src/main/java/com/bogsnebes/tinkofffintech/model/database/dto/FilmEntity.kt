package com.bogsnebes.tinkofffintech.model.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bogsnebes.tinkofffintech.model.network.response.Genre

@Entity
data class FilmEntity(
    @PrimaryKey val filmId: Int,
    val nameRu: String?,
    val nameEn: String?,
    val year: String,
    val posterUrlPreview: String,
    val genres: List<Genre>
)