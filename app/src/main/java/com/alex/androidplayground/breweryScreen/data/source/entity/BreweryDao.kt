package com.alex.androidplayground.breweryScreen.data.source.entity

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface BreweryDao {
    @Insert
    suspend fun insert(brewery: BreweryEntity)

    @Insert
    suspend fun insertAll(breweries: List<BreweryEntity>)

    @Query("DELETE FROM breweries")
    suspend fun deleteAll()

    @Query("SELECT * FROM breweries")
    suspend fun getAll(): List<BreweryEntity>

    @Query("SELECT * FROM breweries WHERE id = :id")
    suspend fun getById(id: String): BreweryEntity?

    @Query("SELECT * FROM breweries")
    fun pagingSource(): PagingSource<Int, BreweryEntity>
}