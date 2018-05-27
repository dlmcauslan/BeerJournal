package com.wordpress.excelenteadventura.beerjournal.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context


@Database(entities = arrayOf(Beer::class), version = 1)
abstract class BeerDataBase: RoomDatabase() {

    abstract fun beerDao(): BeerDao

    companion object {
        private var INSTANCE: BeerDataBase? = null

        fun getInstance(context: Context): BeerDataBase? {
            if (INSTANCE == null) {
                synchronized(BeerDataBase::class) {
                    // Create the database
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            BeerDataBase::class.java,
                            "beer.db").build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }

    }
}