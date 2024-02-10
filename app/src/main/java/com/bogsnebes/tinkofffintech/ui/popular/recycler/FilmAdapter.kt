package com.bogsnebes.tinkofffintech.ui.popular.recycler

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
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
    private val onItemClicked: (Int) -> Unit,
    private val onItemLongClicked: (FilmItem) -> Unit
) :
    ListAdapter<FilmItem, FilmAdapter.FilmViewHolder>(FilmDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return FilmViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val filmItem = getItem(position)
        holder.bind(filmItem, onItemClicked, onItemLongClicked)
    }

    class FilmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val genreTextView: TextView = itemView.findViewById(R.id.genreTextView)
        private val favoriteImageView: ImageView = itemView.findViewById(R.id.imageView3)
        private val posterImageView: ImageView = itemView.findViewById(R.id.imageView2)

        fun bind(
            filmItem: FilmItem,
            onItemClicked: (Int) -> Unit,
            onItemLongClicked: (FilmItem) -> Unit
        ) {
            nameTextView.text = filmItem.film.nameRu
            genreTextView.text =
                "${filmItem.film.genres.joinToString { it.genre }} (${filmItem.film.year})"
            favoriteImageView.visibility = if (filmItem.favorite) View.VISIBLE else View.GONE

            setupPosterImageView(posterImageView, filmItem.film.posterUrlPreview)

            setupItemViewListener(filmItem, onItemClicked = { id ->
                onItemClicked(id)
            }, onItemLongClicked = { filmItem ->
                onItemLongClicked(filmItem)
            })
        }

        private fun setupPosterImageView(posterImageView: ImageView, posterUrlPreview: String) {
            val radiusPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 5f, itemView.context.resources.displayMetrics
            )

            posterImageView.load(posterUrlPreview) {
                crossfade(true)
                crossfade(300)
                transformations(RoundedCornersTransformation(radiusPx))
                error(R.drawable.ic_error)
                scale(Scale.FILL)
            }
        }

        private fun setupItemViewListener(
            filmItem: FilmItem,
            onItemClicked: (Int) -> Unit,
            onItemLongClicked: (FilmItem) -> Unit
        ) {
            itemView.setOnClickListener {
                it.animate().scaleX(0.85f).scaleY(0.85f).setDuration(150).withEndAction {
                    it.animate().scaleX(1f).scaleY(1f).setDuration(150).start()
                    onItemClicked(filmItem.film.filmId)
                }.start()
            }

            itemView.setOnLongClickListener {
                it.animate().scaleX(0.95f).scaleY(0.95f).setDuration(200).withEndAction {
                    onItemLongClicked(filmItem)
                    favoriteImageView.visibility =
                        if (!filmItem.favorite) View.VISIBLE else View.GONE
                }.start()
                true
            }

            itemView.setOnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        view.animate().scaleX(1f).scaleY(1f).setDuration(200).start()
                    }
                }
                false
            }
        }
    }
}
