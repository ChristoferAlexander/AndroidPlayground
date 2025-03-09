package com.alex.androidplayground.di

import com.alex.androidplayground.core.di.ApiModule
import com.alex.androidplayground.mocks.weatherScreen.data.source.remote.api.WeatherApiMock
import com.alex.androidplayground.weatherScreen.data.source.remote.api.WeatherApi
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
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
}
