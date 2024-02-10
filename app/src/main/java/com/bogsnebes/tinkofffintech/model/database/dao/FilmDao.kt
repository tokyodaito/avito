package com.bogsnebes.tinkofffintech.model.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bogsnebes.tinkofffintech.model.database.dto.FilmEntity
import com.bogsnebes.tinkofffintech.model.database.dto.FilmResponseEntity
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface FilmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFilm(film: FilmEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFilmResponse(filmResponse: FilmResponseEntity)

    @Query("SELECT * FROM FilmEntity WHERE filmId = :id")
    fun getFilm(id: Int): Single<FilmEntity>

    @Query("SELECT * FROM FilmResponseEntity WHERE kinopoiskId = :id")
    fun getFilmResponse(id: Long): Single<FilmResponseEntity>

    @Query("SELECT EXISTS(SELECT * FROM FilmEntity WHERE filmId = :id)")
    fun isFavorite(id: Int): Single<Boolean>

    @Delete
    fun deleteFilm(film: FilmEntity)

    @Query("SELECT * FROM FilmEntity")
    fun getAllFavouriteFilms(): Flowable<List<FilmEntity>>

    @Query("SELECT * FROM FilmEntity WHERE nameRu LIKE :keyword || '%' OR nameEn LIKE :keyword || '%'")
    fun searchFilmsByKeyword(keyword: String): Flowable<List<FilmEntity>>
}

