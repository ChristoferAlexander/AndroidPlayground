package com.alex.androidplayground.mocks.core.di

import com.alex.androidplayground.core.di.LocationModule
import com.alex.androidplayground.core.utils.location.LocationService
import com.alex.androidplayground.mocks.core.utils.location.LocationServiceMock
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [LocationModule::class]
)
object TestLocationModule {

    @Provides
    @Singleton
    fun provideLocationService(): LocationService {
        return LocationServiceMock()
    }

}
