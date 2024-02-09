package com.bogsnebes.tinkofffintech.model.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface FilmService {
    @GET("/api/v2.2/films/top")
    fun getTopFilms(@Query("type") type: String): Single<FilmResponse>
}