package com.bogsnebes.tinkofffintech.ui.information

import android.os.Bundle
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
        (activity as? MainActivity)?.showProgressBar(false)
        arguments?.getInt(ARG_FILM_ID)?.let { id ->
            viewModel.loadFilmInfo(id)
        }
        subscribeUI()
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
                }

                DataState.Loading -> {
                    showProgressBar(true)
                }

                is DataState.Success -> {
                    showProgressBar(false)
                    setupFilmInformation(dataState.data)
                }
            }
        }
    }

    private fun setupFilmInformation(film: FilmResponse) {
        binding.information.visibility = View.VISIBLE
        binding.imageView5.load(film.posterUrl) {
            crossfade(true)
            crossfade(300)
        }

        binding.textView2.text = film.nameRu
        binding.textView3.text = film.description
        binding.textView4.text = "Жанры: ${film.genres}"
        binding.textView4.text = "Страны: ${film.countries}"
    }

    private fun showProgressBar(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    companion object {
        private const val ARG_FILM_ID = "film_id"

        fun newInstance(id: Int): InformationFragment =
            InformationFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_FILM_ID, id)
                }
            }
    }
}