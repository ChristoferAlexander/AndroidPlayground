package com.alex.androidplayground.core.di

import com.alex.androidplayground.core.utils.coroutines.DispatcherProvider
import com.alex.androidplayground.core.utils.coroutines.StandardDispatcherProvider
import com.alex.androidplayground.weatherScreen.data.repository.WeatherRepositoryImp
import com.alex.androidplayground.weatherScreen.data.source.remote.api.WeatherApi
import com.alex.androidplayground.weatherScreen.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
    fun provideWeatherRepository(api: WeatherApi, dispatcherProvider: DispatcherProvider): WeatherRepository {
        return WeatherRepositoryImp(api, dispatcherProvider)
    }
}
