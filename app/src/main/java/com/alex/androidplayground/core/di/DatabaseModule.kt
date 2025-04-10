package com.alex.androidplayground.core.di

import android.content.Context
import androidx.room.Room
import com.alex.androidplayground.breweryScreen.data.source.entity.BreweryDao
import com.alex.androidplayground.breweryScreen.data.source.entity.BreweryRemoteKeysDao
import com.alex.androidplayground.core.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Production Module
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    fun provideBreweryDao(database: AppDatabase): BreweryDao {
        return database.breweryDao()
    }

    @Provides
    fun provideBreweryRemoteKeysDao(database: AppDatabase): BreweryRemoteKeysDao {
        return database.breweryRemoteKeysDao()
    }
}