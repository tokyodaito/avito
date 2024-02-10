package com.bogsnebes.tinkofffintech.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bogsnebes.tinkofffintech.R
import com.bogsnebes.tinkofffintech.databinding.ActivityMainBinding
import com.bogsnebes.tinkofffintech.ui.favourites.FavouritesFragment
import com.bogsnebes.tinkofffintech.ui.information.InformationFragment
import com.bogsnebes.tinkofffintech.ui.popular.PopularFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        openPopularFragment()
        setupButtonOpenPopular()
        setupButtonOpenFavourites()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view_tag, InformationFragment.newInstance(321))
            .commit()
    }

    private fun openPopularFragment() {
        showProgressBar(true)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view_tag, PopularFragment.newInstance())
            .commit()
    }

    private fun openFavouritesFragment() {
        showProgressBar(true)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view_tag, FavouritesFragment.newInstance())
            .commit()
    }

    private fun setupButtonOpenPopular() {
        binding.appCompatButton2.setOnClickListener {
            openPopularFragment()
        }
    }

    private fun setupButtonOpenFavourites() {
        binding.appCompatButton.setOnClickListener {
            openFavouritesFragment()
        }
    }

    fun showProgressBar(show: Boolean) {
        binding.fragmentContainerViewTag.visibility = if (show) View.GONE else View.VISIBLE
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}