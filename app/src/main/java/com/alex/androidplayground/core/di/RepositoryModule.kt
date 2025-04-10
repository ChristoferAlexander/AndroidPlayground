package com.alex.androidplayground.core.di

import android.content.Context
import com.alex.androidplayground.breweryScreen.data.repository.BreweryRepositoryImpl
import com.alex.androidplayground.breweryScreen.data.source.api.BreweryApi
import com.alex.androidplayground.core.data.local.AppDatabase
import com.alex.androidplayground.core.utils.coroutines.DispatcherProvider
import com.alex.androidplayground.core.utils.coroutines.StandardDispatcherProvider
import com.alex.androidplayground.foregroundServiceScreen.data.repository.ForegroundServiceStatusRepositoryImp
import com.alex.androidplayground.foregroundServiceScreen.domain.repository.ForegroundServiceStatusRepository
import com.alex.androidplayground.weatherScreen.data.repository.WeatherRepositoryImp
import com.alex.androidplayground.weatherScreen.data.source.remote.WeatherApi
import com.alex.androidplayground.weatherScreen.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Production Module
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider {
        return StandardDispatcherProvider
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(
        api: WeatherApi,
        dispatcherProvider: DispatcherProvider
    ): WeatherRepository {
        return WeatherRepositoryImp(api, dispatcherProvider)
    }

    @Provides
    @Singleton
    fun provideBreweryRepository(
        api: BreweryApi,
        database: AppDatabase
    ): BreweryRepositoryImpl {
        return BreweryRepositoryImpl(api, database)
    }

    @Provides
    @Singleton
    fun provideForegroundServiceStatusRepository(
        @ApplicationContext context: Context,
        dispatcherProvider: DispatcherProvider
    ): ForegroundServiceStatusRepository {
        return ForegroundServiceStatusRepositoryImp(context, dispatcherProvider)
    }
}
