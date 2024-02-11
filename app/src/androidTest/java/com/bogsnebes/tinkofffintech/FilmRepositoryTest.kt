package com.bogsnebes.tinkofffintech

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.bogsnebes.tinkofffintech.model.database.dao.FilmDao
import com.bogsnebes.tinkofffintech.model.imlp.FilmRepository
import com.bogsnebes.tinkofffintech.model.network.FilmService
import com.bogsnebes.tinkofffintech.model.network.response.TopFilmsResponse
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.reactivex.Single
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@LargeTest
class FilmRepositoryTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var filmRepository: FilmRepository
    private val filmService = mock(FilmService::class.java)
    private val filmDao = mock(FilmDao::class.java)

    @Before
    fun setUp() {
        HiltAndroidRule(this).apply {
            inject()
        }
        filmRepository = FilmRepository(filmService, filmDao)
    }

    @Test
    fun getTopFilms_success() {
        val mockResponse = mock(TopFilmsResponse::class.java)
        `when`(filmService.getTopFilms("TOP_100_POPULAR_FILMS", 1)).thenReturn(
            Single.just(
                mockResponse
            )
        )

        val testObserver = filmRepository.getTopFilms().test()

        testObserver.assertNoErrors()
        testObserver.assertValue(mockResponse)
    }

    @Test
    fun getTopFilms_error() {
        val errorResponse = Throwable("Error")
        `when`(
            filmService.getTopFilms(
                anyString(),
                anyInt()
            )
        ).thenReturn(Single.error(errorResponse))

        val testObserver = filmRepository.getTopFilms().test()

        testObserver.assertError(errorResponse)
    }
}
