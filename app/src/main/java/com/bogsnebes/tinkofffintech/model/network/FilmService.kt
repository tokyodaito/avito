package com.bogsnebes.tinkofffintech.model.network

import com.bogsnebes.tinkofffintech.model.network.response.FilmResponse
import com.bogsnebes.tinkofffintech.model.network.response.TopFilmsResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FilmService {

    @GET("v2.2/films/top")
    fun getTopFilms(@Query("type") type: String, @Query("page") page: Int): Single<TopFilmsResponse>

    @GET("v2.2/films/{id}")
    fun getFilmInfo(@Path("id") id: Int): Single<FilmResponse>

    @GET("v2.1/films/search-by-keyword")
    fun searchFilmsByKeyword(
        @Query("keyword") keyword: String,
        @Query("page") page: Int = 1
    ): Single<TopFilmsResponse>
}