package com.bogsnebes.tinkofffintech.model.imlp

import com.bogsnebes.tinkofffintech.model.network.FilmService
import com.bogsnebes.tinkofffintech.model.network.response.FilmResponse
import com.bogsnebes.tinkofffintech.model.network.response.TopFilmsResponse
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class FilmRepository @Inject constructor(
    private val filmService: FilmService
) {
    fun getTopFilms(page: Int = 1): Single<TopFilmsResponse> =
        filmService.getTopFilms("TOP_100_POPULAR_FILMS", page).subscribeOn(
            Schedulers.io()
        )

    fun getFilmInfo(id: Int): Single<FilmResponse> =
        filmService.getFilmInfo(id).subscribeOn(
            Schedulers.io()
        )
}