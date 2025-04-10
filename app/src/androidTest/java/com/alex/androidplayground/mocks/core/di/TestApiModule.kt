package com.alex.androidplayground.mocks.core.di

import com.alex.androidplayground.breweryScreen.data.source.api.BreweryApi
import com.alex.androidplayground.core.di.ApiModule
import com.alex.androidplayground.mocks.breweryScreen.data.source.api.BreweryApiMock
import com.alex.androidplayground.mocks.weatherScreen.data.source.api.WeatherApiMock
import com.alex.androidplayground.weatherScreen.data.source.remote.WeatherApi
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import retrofit2.Retrofit
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
    fun provideBreweryApi(): BreweryApi {
        return BreweryApiMock()
    }
}
