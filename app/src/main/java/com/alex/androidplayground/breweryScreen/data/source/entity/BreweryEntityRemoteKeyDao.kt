package com.alex.androidplayground.breweryScreen.data.source.entity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BreweryRemoteKeysDao {

    @Query("SELECT * FROM breweries_remote_keys WHERE id =:id")
    suspend fun getById(id: String): BreweryEntityRemoteKey?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(remoteKeys: List<BreweryEntityRemoteKey>)

    @Query("DELETE FROM breweries_remote_keys")
    suspend fun deleteAll()
}