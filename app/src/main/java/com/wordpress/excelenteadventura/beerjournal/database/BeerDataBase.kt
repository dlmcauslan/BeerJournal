package com.wordpress.excelenteadventura.beerjournal.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.util.Log
import com.wordpress.excelenteadventura.beerjournal.BeerRepository

@Database(entities = [(Beer::class)], version = 1)
abstract class BeerDataBase: RoomDatabase() {

    @Volatile
    private var INSTANCE: BeerDataBase? = null

    abstract fun beerDao(): BeerDao

//    companion object {
//        private val LOG_TAG = BeerRepository::class.java.simpleName
//        private var INSTANCE: BeerDataBase? = null
//
//        fun getDatabase(context: Context): BeerDataBase {
//            Log.d(LOG_TAG, "Getting the database")
//            if (INSTANCE == null) {
//                synchronized(BeerDataBase::class) {
//                    // Create the database
//                    INSTANCE = Room.databaseBuilder(context.applicationContext,
//                            BeerDataBase::class.java,
//                            "beer.db").build()
//                    Log.d(LOG_TAG, "Creating the database")
//                }
//            }
//            return INSTANCE!!
//        }
//    }
}