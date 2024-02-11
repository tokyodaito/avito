package com.bogsnebes.tinkofffintech.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bogsnebes.tinkofffintech.model.database.converters.Converter
import com.bogsnebes.tinkofffintech.model.database.dao.FilmDao
import com.bogsnebes.tinkofffintech.model.database.dto.FilmEntity
import com.bogsnebes.tinkofffintech.model.database.dto.FilmResponseEntity

@Database(
    entities = [FilmEntity::class, FilmResponseEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun filmDao(): FilmDao
}
