package com.bogsnebes.tinkofffintech.model.network

import com.bogsnebes.tinkofffintech.model.network.response.FilmResponse
import com.bogsnebes.tinkofffintech.model.network.response.TopFilmsResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FilmService {
    @GET("films/top")
    fun getTopFilms(@Query("type") type: String): Single<TopFilmsResponse>

    @GET("films/{id}")
    fun getFilmInfo(@Path("id") id: Int): Single<FilmResponse>
}