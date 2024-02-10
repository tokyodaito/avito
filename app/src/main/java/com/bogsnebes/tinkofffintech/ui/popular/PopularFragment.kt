package com.bogsnebes.tinkofffintech.ui.popular

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bogsnebes.tinkofffintech.R
import com.bogsnebes.tinkofffintech.databinding.FragmentPopularBinding
import com.bogsnebes.tinkofffintech.ui.MainActivity
import com.bogsnebes.tinkofffintech.ui.favourites.recycler.FilmAdapter
import com.bogsnebes.tinkofffintech.ui.information.InformationFragment
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
        }
        subscribeUI()
        setupUpdateButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun subscribeUI() {
        viewModel.films.observe(viewLifecycleOwner) { dataState ->
            when (dataState) {
                is DataState.Success -> {
                    showProgressBar(false)
                    showError(false)
                    setupRecyclerFilms().submitList(dataState.data)
                }

                DataState.Loading -> {
                    showProgressBar(true)
                    showError(false)
                }

                is DataState.Error -> {
                    showProgressBar(false)
                    showError(true)
                }
            }
        }
    }

    private fun setupRecyclerFilms(): FilmAdapter {
        val adapter = FilmAdapter { id ->
            openInformationFragment(id)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }

        return adapter
    }

    private fun setupUpdateButton() {
        binding.button.setOnClickListener {
            viewModel.loadTopFilms()
        }
    }

    private fun showProgressBar(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showError(show: Boolean) {
        binding.error.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun openInformationFragment(id: Int) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view_tag, InformationFragment.newInstance(id))
            .addToBackStack(null)
            .commit()
    }

    companion object {
        fun newInstance(): PopularFragment {
            return PopularFragment()
        }
    }
}