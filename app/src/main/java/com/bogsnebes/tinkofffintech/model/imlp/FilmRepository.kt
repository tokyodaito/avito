package com.bogsnebes.tinkofffintech.model.imlp

import com.bogsnebes.tinkofffintech.model.network.FilmResponse
import com.bogsnebes.tinkofffintech.model.network.FilmService
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class FilmRepository @Inject constructor(
    private val filmService: FilmService
) {
    fun getTopFilms(): Single<FilmResponse> =
        filmService.getTopFilms("TOP_100_POPULAR_FILMS").subscribeOn(
            Schedulers.io()
        )
}