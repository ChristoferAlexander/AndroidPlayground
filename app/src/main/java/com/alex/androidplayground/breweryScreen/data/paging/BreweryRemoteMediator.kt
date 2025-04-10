package com.alex.androidplayground.breweryScreen.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.alex.androidplayground.breweryScreen.data.source.api.BreweryApi
import com.alex.androidplayground.breweryScreen.data.source.api.toEntityList
import com.alex.androidplayground.breweryScreen.data.source.entity.BreweryEntity
import com.alex.androidplayground.breweryScreen.data.source.entity.BreweryEntityRemoteKey
import com.alex.androidplayground.core.data.local.AppDatabase
import kotlinx.coroutines.CancellationException

@ExperimentalPagingApi
class BreweryRemoteMediator(
    private val query: String?, private val pageSize: Int, private val api: BreweryApi, private val database: AppDatabase
) : RemoteMediator<Int, BreweryEntity>() {

    private val dao = database.breweryDao()
    private val remoteKeysDao = database.breweryRemoteKeysDao()

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, BreweryEntity>
    ): MediatorResult {
        return try {
            val currentPage = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKey = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKey?.nextPage?.minus(1) ?: 1
                }

                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevPage = remoteKeys?.prevPage ?: return MediatorResult.Success(
                        endOfPaginationReached = remoteKeys != null
                    )
                    prevPage
                }

                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextPage = remoteKeys?.nextPage ?: return MediatorResult.Success(
                        endOfPaginationReached = remoteKeys != null
                    )
                    nextPage
                }
            }
            val response = if (query.isNullOrEmpty()) {
                api.getBreweries(page = currentPage, perPage = pageSize)
            } else {
                api.getBreweries(query = query, page = currentPage, perPage = pageSize)
            }
            val endOfPaginationReached = response.isEmpty()
            val prevPage = if (currentPage == 1) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    dao.deleteAll()
                    remoteKeysDao.deleteAll()
                }
                val keys = response.map { brewery ->
                    BreweryEntityRemoteKey(
                        id = brewery.id, prevPage = prevPage, nextPage = nextPage
                    )
                }
                remoteKeysDao.add(remoteKeys = keys)
                dao.insertAll(breweries = response.toEntityList())
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, BreweryEntity>
    ): BreweryEntityRemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                remoteKeysDao.getById(id = id)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, BreweryEntity>
    ): BreweryEntityRemoteKey? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { brewery ->
            remoteKeysDao.getById(id = brewery.id)
        }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, BreweryEntity>
    ): BreweryEntityRemoteKey? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { brewery ->
            remoteKeysDao.getById(id = brewery.id)
        }
    }
}