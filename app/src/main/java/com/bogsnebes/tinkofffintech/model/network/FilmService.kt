package com.bogsnebes.tinkofffintech.model.network

import com.bogsnebes.tinkofffintech.model.network.response.FilmResponse
import com.bogsnebes.tinkofffintech.model.network.response.TopFilmsResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FilmService {

    @GET("movie")
    fun getTopFilms(
        @Query("page") page: Int,
        @Query("sortField") sortField: String?,
        @Query("sortType") sortType: Byte?
    ): Single<TopFilmsResponse>

    @GET("movie/{id}")
    fun getFilmInfo(@Path("id") id: Int): Single<FilmResponse>

    @GET("movie/search")
    fun searchFilmsByKeyword(
        @Query("query") keyword: String,
        @Query("page") page: Int = 1
    ): Single<TopFilmsResponse>
}