package com.alex.androidplayground.breweryScreen.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import com.alex.androidplayground.breweryScreen.data.paging.BreweryRemoteMediator
import com.alex.androidplayground.breweryScreen.data.source.api.BreweryApi
import com.alex.androidplayground.breweryScreen.data.source.entity.toBrewery
import com.alex.androidplayground.breweryScreen.domain.repository.BreweryRepository
import com.alex.androidplayground.core.data.local.AppDatabase
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BreweryRepositoryImpl @Inject constructor(
    private val api: BreweryApi,
    private val database: AppDatabase
) : BreweryRepository {

    private val pageSize = 10

    @OptIn(ExperimentalPagingApi::class)
    override fun getBreweriesPagingData(query: String?) =
        Pager(
            config = PagingConfig(pageSize = pageSize),
            remoteMediator = BreweryRemoteMediator(
                query = query,
                pageSize = pageSize,
                api = api,
                database = database
            ),
            pagingSourceFactory = { database.breweryDao().pagingSource() }
        ).flow.map { pagingData ->
            pagingData.map { it.toBrewery() }
        }
}
