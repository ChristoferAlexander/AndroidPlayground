package com.alex.androidplayground.breweryScreen.data.source.api

import com.alex.androidplayground.breweryScreen.data.source.entity.BreweryEntity

data class BreweryDto(
    val id: String,
    val name: String,
    val brewery_type: String,
    val address_1: String?,
    val address_2: String?,
    val address_3: String?,
    val city: String?,
    val state_province: String?,
    val postal_code: String?,
    val country: String?,
    val longitude: String?,
    val latitude: String?,
    val phone: String?,
    val website_url: String?,
    val state: String?,
    val street: String?
)

fun BreweryDto.toEntity(): BreweryEntity {
    return BreweryEntity(
        id = this.id,
        name = this.name,
        breweryType = this.brewery_type,  // Store type as a string
        address1 = this.address_1,
        address2 = this.address_2,
        address3 = this.address_3,
        city = this.city,
        stateProvince = this.state_province,
        postalCode = this.postal_code,
        country = this.country,
        longitude = this.longitude,
        latitude = this.latitude,
        phone = this.phone,
        websiteUrl = this.website_url,
        state = this.state,
        street = this.street
    )
}

fun List<BreweryDto>.toEntityList(): List<BreweryEntity> {
    return this.map { it.toEntity() }
}