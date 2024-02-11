package com.bogsnebes.tinkofffintech.model.imlp

import com.bogsnebes.tinkofffintech.model.database.dao.FilmDao
import com.bogsnebes.tinkofffintech.model.database.dto.FilmEntity
import com.bogsnebes.tinkofffintech.model.network.FilmService
import com.bogsnebes.tinkofffintech.model.network.response.Film
import com.bogsnebes.tinkofffintech.model.network.response.FilmResponse
import com.bogsnebes.tinkofffintech.model.network.response.TopFilmsResponse
import com.bogsnebes.tinkofffintech.ui.favourites.recycler.FilmItem
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class FilmRepository @Inject constructor(
    private val filmService: FilmService,
    private val filmDao: FilmDao
) {
    fun getTopFilms(page: Int = 1): Single<TopFilmsResponse> =
        filmService.getTopFilms("TOP_100_POPULAR_FILMS", page).subscribeOn(
            Schedulers.io()
        )

    fun getFilmInfo(id: Int): Single<FilmResponse> =
        filmService.getFilmInfo(id).subscribeOn(
            Schedulers.io()
        )

    fun saveFilmAsFavorite(film: Film): Completable {
        val filmEntity =
            FilmEntity(
                film.filmId,
                film.nameRu,
                film.nameEn,
                film.year,
                film.posterUrlPreview,
                film.genres
            )
        return Completable.fromAction { filmDao.insertFilm(filmEntity) }
            .subscribeOn(Schedulers.io())
    }

    fun isFilmFavorite(id: Int): Single<Boolean> =
        filmDao.isFavorite(id)
            .subscribeOn(Schedulers.io())

    fun removeFilmFromFavorites(filmId: Int): Completable =
        filmDao.getFilm(filmId).flatMapCompletable { filmEntity ->
            Completable.fromAction { filmDao.deleteFilm(filmEntity) }
        }.subscribeOn(Schedulers.io())

    fun searchFilmsByKeyword(keyword: String, page: Int = 1): Single<TopFilmsResponse> =
        filmService.searchFilmsByKeyword(keyword, page).subscribeOn(Schedulers.io())

    fun searchFilmsInDatabaseByKeyword(keyword: String): Flowable<List<FilmItem>> =
        filmDao.searchFilmsByKeyword(keyword).map { entities ->
            entities.map { entity ->
                FilmItem(
                    film = Film(
                        filmId = entity.filmId,
                        nameRu = entity.nameRu,
                        nameEn = entity.nameEn,
                        year = entity.year,
                        genres = entity.genres,
                        posterUrlPreview = entity.posterUrlPreview
                    ),
                    favorite = true
                )
            }
        }.subscribeOn(Schedulers.io())

    fun getFavouriteFilms(): Flowable<List<FilmItem>> =
        filmDao.getAllFavouriteFilms()
            .map { entities ->
                entities.map { entity ->
                    FilmItem(
                        film = Film(
                            filmId = entity.filmId,
                            nameRu = entity.nameRu,
                            nameEn = entity.nameEn,
                            year = entity.year,
                            genres = entity.genres,
                            posterUrlPreview = entity.posterUrlPreview
                        ),
                        favorite = true
                    )
                }
            }
            .subscribeOn(Schedulers.io())
}