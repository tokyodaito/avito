package com.bogsnebes.tinkofffintech.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bogsnebes.tinkofffintech.R
import com.bogsnebes.tinkofffintech.databinding.ActivityMainBinding
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
    }

    private fun openPopularFragment() {
        showProgressBar(true)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view_tag, PopularFragment.newInstance())
            .commit()
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