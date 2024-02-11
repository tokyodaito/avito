package com.bogsnebes.tinkofffintech.model.imlp

import android.util.Log
import com.bogsnebes.tinkofffintech.model.database.dao.FilmDao
import com.bogsnebes.tinkofffintech.model.database.dto.FilmEntity
import com.bogsnebes.tinkofffintech.model.database.dto.FilmResponseEntity
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

    private fun getAndConvertFilmInfo(filmId: Int): Completable =
        getFilmInfo(filmId).flatMapCompletable { filmResponse ->
            val filmResponseEntity = FilmResponseEntity(
                kinopoiskId = filmResponse.kinopoiskId
                    ?: 95323,
                nameRu = filmResponse.nameRu,
                nameEn = filmResponse.nameEn,
                nameOriginal = filmResponse.nameOriginal,
                posterUrl = filmResponse.posterUrl,
                description = filmResponse.description,
                shortDescription = filmResponse.shortDescription
            )
            Log.d(
                "FilmRepository",
                "Saving film response info for ID: ${filmResponseEntity.kinopoiskId}"
            )
            saveFilmResponseInfo(filmResponseEntity)
        }

    private fun saveFilmResponseInfo(filmResponseEntity: FilmResponseEntity): Completable =
        Completable.fromAction {
            Log.d("FilmRepository", "Inserting film response: ${filmResponseEntity.kinopoiskId}")
            filmDao.insertFilmResponse(filmResponseEntity)
        }.doOnError { error ->
            Log.e("FilmRepository", "Error inserting film response: ${error.localizedMessage}")
        }

    fun saveFilmAsFavorite(film: Film): Completable {
        val filmEntity = FilmEntity(
            film.filmId,
            film.nameRu,
            film.nameEn,
            film.year,
            film.posterUrlPreview,
            film.genres
        )

        Log.d("FilmRepository", "Starting to save film as favorite: ${filmEntity.filmId}")

        return getAndConvertFilmInfo(film.filmId)
            .andThen(saveFilm(filmEntity))
            .doOnComplete {
                Log.d("FilmRepository", "Film saved as favorite successfully: ${filmEntity.filmId}")
            }
            .doOnError { error ->
                Log.e("FilmRepository", "Error saving film as favorite: ${error.localizedMessage}")
            }
            .subscribeOn(Schedulers.io())
    }

    private fun saveFilm(filmEntity: FilmEntity): Completable =
        Completable.fromAction {
            Log.d("FilmRepository", "Inserting film: ${filmEntity.filmId}")
            filmDao.insertFilm(filmEntity)
        }.doOnError { error ->
            Log.e("FilmRepository", "Error inserting film: ${error.localizedMessage}")
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