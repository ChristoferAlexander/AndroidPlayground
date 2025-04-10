package com.alex.androidplayground.mocks.core.di

import com.alex.androidplayground.core.di.ConnectivityModule
import com.alex.androidplayground.core.utils.connectivity.ConnectivityObserver
import com.alex.androidplayground.mocks.core.utils.connectivity.ConnectivityObserverMock
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton


@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [ConnectivityModule::class]
)
object TestConnectivityModule {

    @Provides
    @Singleton
    fun provideConnectivityObserver(): ConnectivityObserver {
        return ConnectivityObserverMock()
    }
}
