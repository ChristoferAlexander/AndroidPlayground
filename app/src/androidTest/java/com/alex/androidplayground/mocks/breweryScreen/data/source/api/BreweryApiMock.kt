package com.alex.androidplayground.mocks.breweryScreen.data.source.api

import com.alex.androidplayground.breweryScreen.data.source.api.BreweryApi
import com.alex.androidplayground.breweryScreen.data.source.api.BreweryDto

class BreweryApiMock : BreweryApi {

    var breweriesToReturn: List<BreweryDto> = listOf(
        BreweryDto(
            id = "123",
            name = "Cool Brew Co",
            brewery_type = "micro",
            address_1 = "123 Beer Street",
            address_2 = null,
            address_3 = null,
            city = "Hopsville",
            state_province = "AleState",
            postal_code = "12345",
            country = "Brewtopia",
            longitude = "45.0",
            latitude = "12.0",
            phone = "1234567890",
            website_url = "https://coolbrew.example.com",
            state = "AleState",
            street = "123 Beer Street"
        )
    )

    override suspend fun getBreweries(
        query: String?,
        page: Int,
        perPage: Int
    ): List<BreweryDto> {
        return breweriesToReturn
            .filter { query.isNullOrBlank() || it.name.contains(query, ignoreCase = true) }
            .drop((page - 1) * perPage)
            .take(perPage)
    }

    override suspend fun getBreweries(
        page: Int,
        perPage: Int
    ): List<BreweryDto> {
        return breweriesToReturn
            .drop((page - 1) * perPage)
            .take(perPage)
    }
}