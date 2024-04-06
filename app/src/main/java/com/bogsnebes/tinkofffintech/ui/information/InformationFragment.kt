package com.bogsnebes.tinkofffintech.ui.information

import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import com.bogsnebes.tinkofffintech.databinding.FragmentInformationBinding
import com.bogsnebes.tinkofffintech.model.network.response.FilmResponse
import com.bogsnebes.tinkofffintech.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InformationFragment : Fragment() {
    private var _binding: FragmentInformationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InformationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = FragmentInformationBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.let {
            it.showProgressBar(false)
            if (checkLandscape())
                it.showBottomNavigation(true)
            else
                it.showBottomNavigation(false)
            it.setupLandscapeListener()
        }
        arguments?.getInt(ARG_FILM_ID)?.let { id ->
            viewModel.loadFilmInfo(id)
        }
        subscribeUI()
        setupBackButton()
        setupLandscapeSettings()
        setupReloadButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun subscribeUI() {
        viewModel.film.observe(viewLifecycleOwner) { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    showProgressBar(false)
                    showError(true)
                }

                DataState.Loading -> {
                    showProgressBar(true)
                    showError(false)
                }

                is DataState.Success -> {
                    showProgressBar(false)
                    showError(false)
                    setupFilmInformation(dataState.data)
                }
            }
        }
    }

    private fun setupFilmInformation(film: FilmResponse) {
        binding.information.visibility = View.VISIBLE
        binding.imageView5.load(film.posterUrl?.url) {
            crossfade(true)
            crossfade(300)
        }

        binding.textView2.text = if (film.nameRu.isNullOrEmpty()) "Нет информации" else film.nameRu
        binding.textView3.text =
            if (film.description.isNullOrEmpty()) "Нет информации" else film.description
        binding.textView4.text =
            createGenresText(film.genres?.joinToString(separator = ", ") { it.genre }
                ?: "Нет данных")
        binding.textView5.text =
            createCountriesText(film.countries?.joinToString(separator = ", ") { it.country }
                ?: "Нет данных")
    }

    private fun setupReloadButton() {
        binding.button.setOnClickListener {
            arguments?.getInt(ARG_FILM_ID)?.let { id ->
                viewModel.loadFilmInfo(id)
            }
        }
    }

    private fun createGenresText(genres: String): SpannableString {
        val genresText = "Жанры: $genres"

        return SpannableString(genresText).apply {
            setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                "Жанры:".length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun createCountriesText(countries: String): SpannableString {
        val countriesText = "Страны: $countries"

        return SpannableString(countriesText).apply {
            setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                "Страны:".length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun showProgressBar(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showError(show: Boolean) {
        binding.error.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun setupBackButton() {
        binding.imageView4.setOnClickListener {
            (activity as? MainActivity)?.supportFragmentManager?.popBackStack()
        }
    }

    private fun setupLandscapeSettings() {
        if (checkLandscape()) {
            binding.imageView4.visibility = View.GONE
        } else {
            binding.imageView4.visibility = View.VISIBLE
        }
    }

    private fun checkLandscape(): Boolean {
        return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    companion object {
        const val ARG_FILM_ID = "film_id"

        fun newInstance(id: Int): InformationFragment =
            InformationFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_FILM_ID, id)
                }
            }
    }
}