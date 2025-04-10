package com.alex.androidplayground.breweryScreen.domain.model

data class Brewery(
    val id: String,
    val name: String,
    val breweryType: BreweryType,
    val address1: String? = null,
    val address2: String? = null,
    val address3: String? = null,
    val city: String? = null,
    val stateProvince: String? = null,
    val postalCode: String? = null,
    val country: String? = null,
    val longitude: String? = null,
    val latitude: String? = null,
    val phone: String? = null,
    val websiteUrl: String? = null,
    val state: String? = null,
    val street: String? = null
)