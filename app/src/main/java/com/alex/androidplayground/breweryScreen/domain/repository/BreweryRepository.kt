package com.alex.androidplayground.breweryScreen.domain.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.alex.androidplayground.breweryScreen.domain.model.Brewery
import kotlinx.coroutines.flow.Flow

interface BreweryRepository{

    @OptIn(ExperimentalPagingApi::class)
    fun getBreweriesPagingData(query: String? = null) : Flow<PagingData<Brewery>>
}