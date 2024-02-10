package com.bogsnebes.tinkofffintech.di.database

import android.content.Context
import androidx.room.Room
import com.bogsnebes.tinkofffintech.model.database.AppDatabase
import com.bogsnebes.tinkofffintech.model.database.dao.FilmDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    fun provideFavoritesDao(appDatabase: AppDatabase): FilmDao {
        return appDatabase.filmDao()
    }
}