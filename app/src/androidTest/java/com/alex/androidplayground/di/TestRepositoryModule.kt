package com.alex.androidplayground.di

import com.alex.androidplayground.core.di.RepositoryModule
import com.alex.androidplayground.mocks.utils.coroutines.TestDispatchersProvider
import com.alex.androidplayground.weatherScreen.data.repository.WeatherRepositoryImp
import com.alex.androidplayground.weatherScreen.data.source.remote.api.WeatherApi
import com.alex.androidplayground.weatherScreen.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
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
    fun provideDispatcherProvider() : TestDispatchersProvider{
        return TestDispatchersProvider(UnconfinedTestDispatcher())
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(api: WeatherApi, dispatchersProvider: TestDispatchersProvider): WeatherRepository {
        return WeatherRepositoryImp(api, dispatchersProvider)
    }
}
