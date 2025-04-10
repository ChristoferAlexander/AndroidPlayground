package com.alex.androidplayground.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alex.androidplayground.breweryScreen.data.source.entity.BreweryDao
import com.alex.androidplayground.breweryScreen.data.source.entity.BreweryEntity
import com.alex.androidplayground.breweryScreen.data.source.entity.BreweryEntityRemoteKey
import com.alex.androidplayground.breweryScreen.data.source.entity.BreweryRemoteKeysDao

@Database(entities = [BreweryEntity::class, BreweryEntityRemoteKey::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun breweryDao(): BreweryDao
    abstract fun breweryRemoteKeysDao(): BreweryRemoteKeysDao
}