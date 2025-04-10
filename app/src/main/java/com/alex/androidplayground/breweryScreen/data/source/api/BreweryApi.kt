package com.alex.androidplayground.breweryScreen.data.source.api

import retrofit2.http.GET
import retrofit2.http.Query

interface BreweryApi {
    @GET("v1/breweries/search/")
    suspend fun getBreweries(
        @Query("query") query: String? = null,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): List<BreweryDto>

    @GET("v1/breweries/")
    suspend fun getBreweries(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): List<BreweryDto>

}