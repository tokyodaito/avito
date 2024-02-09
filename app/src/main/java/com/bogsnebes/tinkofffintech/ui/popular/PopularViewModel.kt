package com.bogsnebes.tinkofffintech.ui.popular

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bogsnebes.tinkofffintech.model.imlp.FilmRepository
import com.bogsnebes.tinkofffintech.ui.popular.recycler.FilmItem
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class PopularViewModel @Inject constructor(
    private val filmRepository: FilmRepository
) : ViewModel() {
    private val _films = MutableLiveData<List<FilmItem>>()
    val films: LiveData<List<FilmItem>> = _films

    private val compositeDisposable = CompositeDisposable()

    init {
        loadTopFilms()
    }

    private fun loadTopFilms() {
        var disposable = filmRepository.getTopFilms()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { response ->
                response.films.map { film ->
                    FilmItem(
                        film,
                        false
                    ) // Предполагается, что изначально фильмы не добавлены в избранное
                }
            }
            .subscribe({ filmItems ->
                _films.value = filmItems
            }, { error ->
                Log.e("PopularViewModel", "Error loading films: ", error)
            })
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}
