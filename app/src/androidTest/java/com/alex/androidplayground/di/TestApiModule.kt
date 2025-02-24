package com.alex.androidplayground.di

import com.alex.androidplayground.di.mocks.weather.WeatherApiMock
import com.alex.androidplayground.weather.api.WeatherApi
import com.alex.androidplayground.weather.service.WeatherService
import com.alex.androidplayground.weather.service.WeatherServiceImp
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [ApiModule::class]
)
object TestApiModule {
    @Provides
    @Singleton
    fun provideMockWeatherApi(): WeatherApi {
        return WeatherApiMock()
    }

    @Provides
    @Singleton
    fun provideWeatherService(api: WeatherApi): WeatherService {
        return WeatherServiceImp(api, Dispatchers.Unconfined)
    }
}
