package com.bogsnebes.tinkofffintech.ui.favourites

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bogsnebes.tinkofffintech.model.imlp.FilmRepository
import com.bogsnebes.tinkofffintech.ui.favourites.recycler.FilmItem
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val filmRepository: FilmRepository
) : ViewModel() {
    private val _films = MutableLiveData<DataState<List<FilmItem>>>()
    val films: LiveData<DataState<List<FilmItem>>> = _films

    private var currentKeyword: String = ""

    private val compositeDisposable = CompositeDisposable()

    var showBack = false

    init {
        loadFavouriteFilms()
    }

    fun loadFavouriteFilms() {
        val disposable = filmRepository.getFavouriteFilms()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { filmItems ->
                    _films.value = DataState.Success(filmItems)
                },
                { error ->
                    _films.value = DataState.Error(error)
                }
            )

        compositeDisposable.add(disposable)
    }

    fun toggleFavoriteStatus(filmItem: FilmItem) {
        val action = filmRepository.removeFilmFromFavorites(filmItem.film.filmId)

        val disposable = action
            .andThen(Single.fromCallable { !filmItem.favorite })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ isNowFavorite ->
                if (_films.value is DataState.Success<*>) {
                    val currentList = (_films.value as DataState.Success<List<FilmItem>>).data
                    val updatedList = currentList.map { item ->
                        if (item.film.filmId == filmItem.film.filmId) item.copy(favorite = isNowFavorite) else item
                    }
                    _films.postValue(DataState.Success(updatedList))
                }
            }, { error ->
                Log.e("PopularViewModel", "Error updating favorite status: ", error)
            })

        compositeDisposable.add(disposable)
    }

    fun searchFilmsByKeyword(keyword: String) {
        if (keyword.isBlank()) {
            return
        }

        currentKeyword = keyword
        _films.postValue(DataState.Loading)

        val disposable = filmRepository.searchFilmsInDatabaseByKeyword(currentKeyword)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ filmItems ->
                if (filmItems.isNotEmpty()) {
                    _films.postValue(DataState.Success(filmItems))
                } else {
                    _films.postValue(DataState.NotFound)
                }
            }, { error ->
                _films.postValue(DataState.Error(error))
            })

        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}
