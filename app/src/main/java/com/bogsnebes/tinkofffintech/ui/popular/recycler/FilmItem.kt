package com.bogsnebes.tinkofffintech.ui.popular.recycler

import com.bogsnebes.tinkofffintech.model.network.Film

data class FilmItem(
    val film: Film,
    var favorite: Boolean
)