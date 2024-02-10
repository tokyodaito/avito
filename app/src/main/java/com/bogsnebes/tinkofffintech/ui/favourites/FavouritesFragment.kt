package com.bogsnebes.tinkofffintech.ui.favourites

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bogsnebes.tinkofffintech.databinding.FragmentFavouritesBinding
import com.bogsnebes.tinkofffintech.ui.MainActivity
import com.bogsnebes.tinkofffintech.ui.favourites.recycler.FilmFavouritesAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouritesFragment : Fragment() {
    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavouritesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = FragmentFavouritesBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.also {
            it.showBottomNavigation(true)
            it.showProgressBar(false)
        }
        subscribeUI(setupRecyclerFilms())
        setupUpdateButton()
        setupSearchWatcher()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun subscribeUI(recyclerAdapter: FilmFavouritesAdapter) {
        viewModel.films.observe(viewLifecycleOwner) { dataState ->
            when (dataState) {
                is DataState.Success -> {
                    showProgressBar(false)
                    showError(false)
                    showNotFound(false)
                    recyclerAdapter.submitList(dataState.data)
                }

                DataState.Loading -> {
                    showProgressBar(true)
                    showError(false)
                    showNotFound(false)
                }

                is DataState.Error -> {
                    showProgressBar(false)
                    showError(true)
                    showNotFound(false)
                }

                DataState.NotFound -> {
                    recyclerAdapter.submitList(listOf())
                    showProgressBar(false)
                    showError(false)
                    showNotFound(true)
                }
            }
        }
    }

    private fun setupRecyclerFilms(): FilmFavouritesAdapter {
        val adapter =
            FilmFavouritesAdapter()


        val layoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        setupBackListener(adapter)

        return adapter
    }

    private fun setupUpdateButton() {
        binding.button.setOnClickListener {
            viewModel.loadFavouriteFilms()
        }
    }

    private fun showProgressBar(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showNotFound(show: Boolean) {
        binding.button2.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showError(show: Boolean) {
        binding.error.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun setupSearchWatcher() {
        binding.toolBar.setOnEditTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                viewModel.showBack = true
                viewModel.searchFilmsByKeyword(s.toString().trim())
            }
        })
    }

    private fun setupBackListener(adapter: FilmFavouritesAdapter) {
        binding.toolBar.setOnBackClickListener {
            adapter.submitList(listOf())
            viewModel.showBack = false
            viewModel.loadFavouriteFilms()
        }
    }

    companion object {
        fun newInstance() = FavouritesFragment()
    }
}