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
    private val _films = MutableLiveData<DataState<List<FilmItem>>>()
    val films: LiveData<DataState<List<FilmItem>>> = _films

    private val compositeDisposable = CompositeDisposable()

    init {
        loadTopFilms()
    }

    fun loadTopFilms() {
        _films.postValue(DataState.Loading)
        val disposable = filmRepository.getTopFilms()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { response ->
                response.films.map { film ->
                    FilmItem(
                        film,
                        false
                    )
                }
            }
            .subscribe({ filmItems ->
                _films.value = DataState.Success(filmItems)
            }, { error ->
                Log.e("PopularViewModel", "Error loading films: ", error)
                _films.value = DataState.Error(error)
            })
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}
