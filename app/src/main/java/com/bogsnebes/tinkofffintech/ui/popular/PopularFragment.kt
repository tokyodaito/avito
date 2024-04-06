package com.bogsnebes.tinkofffintech.ui.popular

import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bogsnebes.tinkofffintech.R
import com.bogsnebes.tinkofffintech.databinding.FragmentPopularBinding
import com.bogsnebes.tinkofffintech.ui.MainActivity
import com.bogsnebes.tinkofffintech.ui.information.InformationFragment
import com.bogsnebes.tinkofffintech.ui.popular.recycler.FilmAdapter
import com.bogsnebes.tinkofffintech.ui.popular.recycler.FilmItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PopularFragment : Fragment() {
    private var _binding: FragmentPopularBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PopularViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = FragmentPopularBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.also {
            it.showBottomNavigation(true)
            it.showProgressBar(false)
            binding.toolBar.setupUI(viewModel.showBack, viewModel.currentKeyword)
        }
        subscribeUI(setupRecyclerFilms())
        setupUpdateButton()
        initSpinner()
        setupSearchWatcher()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun subscribeUI(recyclerAdapter: FilmAdapter) {
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

    private fun setupRecyclerFilms(): FilmAdapter {
        fun onItemLongClicked(film: FilmItem) {
            viewModel.toggleFavoriteStatus(film)
        }

        val adapter =
            FilmAdapter(
                onItemClicked = { id -> openInformationFragment(id) },
                onItemLongClicked = { filmItem ->
                    onItemLongClicked(filmItem)
                })


        val layoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        setupBackListener(adapter)

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!viewModel.loadingPage) {
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                    if (totalItemCount <= (lastVisibleItem + 3)) {
                        if (viewModel.currentKeyword.isNotBlank())
                            viewModel.searchFilmsByKeyword(
                                viewModel.currentKeyword,
                                isNextPage = true
                            )
                        else
                            viewModel.loadTopFilms(isNextPage = true)
                    }
                }
            }
        })

        return adapter
    }

    private fun initSpinner() {
        val items = arrayOf("Показать все", "По дате выхода", "По возрасту", "Страна выпуска")
        binding.spinner.apply {
            adapter = CustomSpinnerAdapter(
                requireContext(), android.R.layout.simple_spinner_item, items
            )
            onItemSelectedListener = createItemSelectedListener()
        }
    }

    private fun createItemSelectedListener() = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>, view: View?, position: Int, id: Long
        ) {
            val sortType = parent.getItemAtPosition(position).toString()
            viewModel.sortFilms(sortType)
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
            // Ничего не выбрано
        }
    }

    private fun setupUpdateButton() {
        binding.button.setOnClickListener {
            viewModel.loadTopFilms()
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

    private fun openInformationFragment(id: Int) {
        if (isAdded) {
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()

            val isLandscape =
                resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

            val containerId = if (isLandscape) {
                R.id.fragment_container_view_tag2
            } else {
                R.id.fragment_container_view_tag
            }

            fragmentTransaction.replace(containerId, InformationFragment.newInstance(id))
            if (!isLandscape)
                fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
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

    private fun setupBackListener(adapter: FilmAdapter) {
        binding.toolBar.setOnBackClickListener {
            adapter.submitList(listOf())
            viewModel.showBack = false
            viewModel.loadTopFilms(false)
        }
    }

    companion object {
        fun newInstance(): PopularFragment {
            return PopularFragment()
        }
    }
}