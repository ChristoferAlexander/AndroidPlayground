package com.alex.androidplayground.breweryScreen.data.source.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alex.androidplayground.breweryScreen.domain.model.Brewery
import com.alex.androidplayground.breweryScreen.domain.model.BreweryType

@Entity(tableName = "breweries")
data class BreweryEntity(
    @PrimaryKey val id: String,
    val name: String,
    @ColumnInfo(name = "brewery_type") val breweryType: String,
    @ColumnInfo(name = "address_1") val address1: String?,
    @ColumnInfo(name = "address_2") val address2: String?,
    @ColumnInfo(name = "address_3") val address3: String?,
    val city: String?,
    @ColumnInfo(name = "state_province") val stateProvince: String?,
    @ColumnInfo(name = "postal_code") val postalCode: String?,
    val country: String?,
    val longitude: String?,
    val latitude: String?,
    val phone: String?,
    @ColumnInfo(name = "website_url") val websiteUrl: String?,
    val state: String?,
    val street: String?
)

fun BreweryEntity.toBrewery(): Brewery {
    val breweryType = this.breweryType.getBreweryType()
    return Brewery(
        id = this.id,
        name = this.name,
        breweryType = breweryType,
        address1 = this.address1,
        address2 = this.address2,
        address3 = this.address3,
        city = this.city,
        stateProvince = this.stateProvince,
        postalCode = this.postalCode,
        country = this.country,
        longitude = this.longitude,
        latitude = this.latitude,
        phone = this.phone,
        websiteUrl = this.websiteUrl,
        state = this.state,
        street = this.street
    )
}

 private fun String.getBreweryType(): BreweryType {
    return when (this) {
        "micro" -> BreweryType.Micro
        "nano" -> BreweryType.Nano
        "regional" -> BreweryType.Regional
        "brewpub" -> BreweryType.Brewpub
        "large" -> BreweryType.Large
        "planning" -> BreweryType.Planning
        "bar" -> BreweryType.Bar
        "contract" -> BreweryType.Contract
        "proprietor" -> BreweryType.Proprietor
        "closed" -> BreweryType.Closed
        "taproom" -> BreweryType.Taproom
        else -> throw IllegalArgumentException("Unknown brewery type: $this")
    }
}
