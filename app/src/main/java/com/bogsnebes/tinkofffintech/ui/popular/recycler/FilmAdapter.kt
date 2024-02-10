package com.bogsnebes.tinkofffintech.ui.favourites.recycler

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import com.bogsnebes.tinkofffintech.R

class FilmAdapter(
    private val onItemClicked: (Int) -> Unit
) :
    ListAdapter<FilmItem, FilmAdapter.FilmViewHolder>(
        FilmDiffCallback
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return FilmViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val filmItem = getItem(position)
        holder.bind(filmItem, onItemClicked)
    }

    class FilmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val genreTextView: TextView = itemView.findViewById(R.id.genreTextView)
        private val favoriteImageView: ImageView = itemView.findViewById(R.id.imageView3)
        private val posterImageView: ImageView = itemView.findViewById(R.id.imageView2)

        fun bind(filmItem: FilmItem, onItemClicked: (Int) -> Unit) {
            nameTextView.text = filmItem.film.nameRu
            genreTextView.text =
                "${filmItem.film.genres.joinToString { it.genre }} (${filmItem.film.year})"
            favoriteImageView.visibility = if (filmItem.favorite) View.VISIBLE else View.GONE

            val radiusPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 5f, itemView.context.resources.displayMetrics
            )

            posterImageView.load(filmItem.film.posterUrlPreview) {
                crossfade(true)
                crossfade(300)
                transformations(RoundedCornersTransformation(radiusPx))
                error(R.drawable.ic_error)
                scale(Scale.FILL)
            }

            itemView.setOnClickListener {
                onItemClicked(filmItem.film.filmId)
            }
        }
    }
}
