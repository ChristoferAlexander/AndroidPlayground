package com.alex.androidplayground.core.di

import com.alex.androidplayground.BuildConfig
import com.alex.androidplayground.breweryScreen.data.source.api.BreweryApi
import com.alex.androidplayground.weatherScreen.data.source.remote.WeatherApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

// Production Module
@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Named("WeatherBaseUrl")
    fun provideWeatherBaseUrl(): String = BuildConfig.WEATHER_BASE_URL

    @Provides
    @Named("BreweryBaseUrl")
    fun provideBreweryBaseUrl(): String = BuildConfig.BREWERY_BASE_URL

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
            else HttpLoggingInterceptor.Level.NONE
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherApi(
        okHttpClient: OkHttpClient,
        @Named("WeatherBaseUrl") baseUrl: String
    ): WeatherApi {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }

    @Provides
    @Singleton
    fun provideBreweryApi(
        okHttpClient: OkHttpClient,
        @Named("BreweryBaseUrl") baseUrl: String
    ): BreweryApi {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BreweryApi::class.java)
    }
}
