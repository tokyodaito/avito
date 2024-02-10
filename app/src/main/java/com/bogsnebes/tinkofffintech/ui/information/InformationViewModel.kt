package com.bogsnebes.tinkofffintech.ui.information

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bogsnebes.tinkofffintech.model.imlp.FilmRepository
import com.bogsnebes.tinkofffintech.model.network.response.FilmResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class InformationViewModel @Inject constructor(
    private val filmRepository: FilmRepository
) : ViewModel() {
    private val _film = MutableLiveData<DataState<FilmResponse>>()
    val film: LiveData<DataState<FilmResponse>> = _film

    private val compositeDisposable = CompositeDisposable()

    fun loadFilmInfo(id: Int) {
        _film.postValue(DataState.Loading)
        val disposable = filmRepository.getFilmInfo(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ filmResponse ->
                _film.value = DataState.Success(filmResponse)
            }, { error ->
                Log.e("PopularViewModel", "Error loading films: ", error)
                _film.value = DataState.Error(error)
            })

        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}