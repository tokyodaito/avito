package com.bogsnebes.tinkofffintech.ui.popular.recycler

import com.bogsnebes.tinkofffintech.model.network.response.Film

data class FilmItem(
    val film: Film,
    var favorite: Boolean
)