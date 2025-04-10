package com.alex.androidplayground.mocks.core.di

import android.content.Context
import com.alex.androidplayground.core.di.RepositoryModule
import com.alex.androidplayground.core.utils.coroutines.DispatcherProvider
import com.alex.androidplayground.foregroundServiceScreen.data.repository.ForegroundServiceStatusRepositoryImp
import com.alex.androidplayground.foregroundServiceScreen.domain.repository.ForegroundServiceStatusRepository
import com.alex.androidplayground.mocks.breweryScreen.data.repository.ForegroundServiceStatusRepositoryMock
import com.alex.androidplayground.mocks.utils.coroutines.TestDispatchersProvider
import com.alex.androidplayground.weatherScreen.data.repository.WeatherRepositoryImp
import com.alex.androidplayground.weatherScreen.data.source.remote.WeatherApi
import com.alex.androidplayground.weatherScreen.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
object TestRepositoryModule {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Provides
    @Singleton
    fun provideDispatcherProvider() : DispatcherProvider {
        return TestDispatchersProvider(UnconfinedTestDispatcher())
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(api: WeatherApi, dispatchersProvider: DispatcherProvider): WeatherRepository {
        return WeatherRepositoryImp(api, dispatchersProvider)
    }

    @Provides
    @Singleton
    fun provideForegroundServiceStatusRepository(): ForegroundServiceStatusRepository {
        return ForegroundServiceStatusRepositoryMock()
    }
}
