package com.bogsnebes.tinkofffintech.ui.favourites.recycler

import androidx.recyclerview.widget.DiffUtil

object FilmDiffCallback : DiffUtil.ItemCallback<FilmItem>() {
    override fun areItemsTheSame(oldItem: FilmItem, newItem: FilmItem): Boolean {
        return oldItem.film.filmId == newItem.film.filmId
    }

    override fun areContentsTheSame(oldItem: FilmItem, newItem: FilmItem): Boolean {
        return oldItem == newItem
    }
}
