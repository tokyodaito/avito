package com.bogsnebes.tinkofffintech.di.network

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import com.bogsnebes.tinkofffintech.model.network.FilmService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://kinopoiskapiunofficial.tech/api/"
    private const val API_KEY = "e30ffed0-76ab-4dd6-b41f-4c9da2b2735b"
    private const val CACHE_SIZE = 10 * 1024 * 1024 // 10 MB

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    @Singleton
    @Provides
    fun provideOkHttpClient(
        @ApplicationContext applicationContext: Context,
        cache: Cache
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val apiKeyInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val requestWithApiKey = originalRequest.newBuilder()
                .header("x-api-key", API_KEY)
                .build()
            chain.proceed(requestWithApiKey)
        }

        val cacheInterceptor = Interceptor { chain ->
            val response = chain.proceed(chain.request())
            val cacheControl = CacheControl.Builder()
                .maxAge(5, TimeUnit.MINUTES)
                .build()
            response.newBuilder()
                .header("Cache-Control", cacheControl.toString())
                .build()
        }

        val dynamicCacheInterceptor = Interceptor { chain ->
            var request = chain.request()
            request = if (isOnline(applicationContext)) {
                request.newBuilder().header("Cache-Control", "public, max-age=" + 5).build()
            } else {
                request.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24)
                    .build()
            }
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .followRedirects(true)
            .cache(cache)
            .addInterceptor(logging)
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(cacheInterceptor)
            .addInterceptor(dynamicCacheInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideCache(application: Application): Cache {
        val cacheDir = File(application.cacheDir, "http_cache")
        return Cache(cacheDir, CACHE_SIZE.toLong())
    }

    @Provides
    @Singleton
    fun provideFilmService(retrofit: Retrofit): FilmService =
        retrofit.create(FilmService::class.java)

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

}