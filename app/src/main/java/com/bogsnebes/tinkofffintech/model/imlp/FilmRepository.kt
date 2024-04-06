package com.bogsnebes.tinkofffintech.model.imlp

import android.util.Log
import com.bogsnebes.tinkofffintech.model.database.dao.FilmDao
import com.bogsnebes.tinkofffintech.model.database.dto.FilmEntity
import com.bogsnebes.tinkofffintech.model.database.dto.FilmResponseEntity
import com.bogsnebes.tinkofffintech.model.network.FilmService
import com.bogsnebes.tinkofffintech.model.network.response.Film
import com.bogsnebes.tinkofffintech.model.network.response.FilmResponse
import com.bogsnebes.tinkofffintech.model.network.response.Poster
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
    fun getTopFilms(
        page: Int = 1,
        sortField: String? = null,
        sortType: Byte? = null
    ): Single<TopFilmsResponse> =
        filmService.getTopFilms(page, sortField, sortType).subscribeOn(
            Schedulers.io()
        )

    private fun getFilmInfo(id: Int): Single<FilmResponse> =
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
                posterUrl = filmResponse.posterUrl?.url,
                description = filmResponse.description,
                shortDescription = null,
                genres = filmResponse.genres ?: listOf(),
                countries = filmResponse.countries ?: listOf()
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
        val filmEntity = film.posterUrlPreview.previewUrl?.let {
            FilmEntity(
                film.filmId,
                film.nameRu,
                film.nameEn,
                film.year,
                it,
                film.genres
            )
        }

        if (filmEntity != null) {
            Log.d("FilmRepository", "Starting to save film as favorite: ${filmEntity.filmId}")
        }

        return getAndConvertFilmInfo(film.filmId)
            .andThen(filmEntity?.let { saveFilm(it) })
            .doOnComplete {
                if (filmEntity != null) {
                    Log.d(
                        "FilmRepository",
                        "Film saved as favorite successfully: ${filmEntity.filmId}"
                    )
                }
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
                        posterUrlPreview = Poster(null, entity.posterUrlPreview)
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
                            posterUrlPreview = Poster(null, entity.posterUrlPreview)
                        ),
                        favorite = true
                    )
                }
            }
            .subscribeOn(Schedulers.io())

    fun getFilmInfoFromDbOrNetwork(id: Int): Single<FilmResponse> =
        getFilmResponseFromDb(id).onErrorResumeNext {
            getFilmInfo(id).doOnSuccess { filmResponse ->
                saveFilmResponseInfo(
                    FilmResponseEntity(
                        kinopoiskId = filmResponse.kinopoiskId ?: 95323,
                        nameRu = filmResponse.nameRu,
                        nameEn = filmResponse.nameEn,
                        nameOriginal = filmResponse.nameOriginal,
                        posterUrl = filmResponse.posterUrl?.url,
                        description = filmResponse.description,
                        shortDescription = null,
                        genres = filmResponse.genres ?: listOf(),
                        countries = filmResponse.countries ?: listOf()
                    )
                ).subscribe({
                    Log.d("FilmRepository", "Film info saved to DB")
                }, {
                    Log.e("FilmRepository", "Error saving film info to DB: ${it.localizedMessage}")
                })
            }
        }

    private fun getFilmResponseFromDb(id: Int): Single<FilmResponse> =
        filmDao.getFilmResponse(id.toLong()).map { entity ->
            FilmResponse(
                kinopoiskId = entity.kinopoiskId,
                nameRu = entity.nameRu,
                nameEn = entity.nameEn,
                nameOriginal = entity.nameOriginal,
                posterUrl = Poster(entity.posterUrl, null),
                description = entity.description,
                shortDescription = entity.shortDescription,
                countries = entity.countries,
                genres = entity.genres
            )
        }

}