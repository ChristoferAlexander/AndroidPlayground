package com.alex.androidplayground.foregroundServiceScreen.data.repository

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.alex.androidplayground.core.utils.coroutines.DispatcherProvider
import com.alex.androidplayground.foregroundServiceScreen.domain.repository.ForegroundServiceStatusRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Singleton
class ForegroundServiceStatusRepositoryImp @Inject constructor(
    @ApplicationContext context: Context,
    private val dispatcherProvider: DispatcherProvider
) : ForegroundServiceStatusRepository {

    private val dataStore = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("foreground_service_prefs") }
    )

    companion object {
        private val IS_RUNNING = booleanPreferencesKey("is_service_running")
    }

    override val isRunning: Flow<Boolean> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs -> prefs[IS_RUNNING] == true }
        .flowOn(dispatcherProvider.io)

    override suspend fun setRunning(isRunning: Boolean)  {
        withContext(dispatcherProvider.io) {
            dataStore.edit { prefs ->
                prefs[IS_RUNNING] = isRunning
            }
        }
    }
}