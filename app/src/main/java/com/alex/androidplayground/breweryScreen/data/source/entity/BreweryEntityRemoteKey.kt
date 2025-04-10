package com.alex.androidplayground.breweryScreen.data.source.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "breweries_remote_keys")
data class BreweryEntityRemoteKey(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val prevPage: Int?,
    val nextPage: Int?
)