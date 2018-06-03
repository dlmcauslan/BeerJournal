package com.wordpress.excelenteadventura.beerjournal.database

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.wordpress.excelenteadventura.beerjournal.BeerRepository

@Database(entities = arrayOf(Beer::class), version = 1)
abstract class BeerDataBase: RoomDatabase() {

    abstract fun beerDao(): BeerDao

    companion object {
        private val LOG_TAG = BeerRepository::class.java.simpleName
        private var INSTANCE: BeerDataBase? = null

        fun getDatabase(context: Context): BeerDataBase {
            Log.d(LOG_TAG, "Getting the database")
            if (INSTANCE == null) {
                synchronized(BeerDataBase::class) {
                    // Create the database
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            BeerDataBase::class.java,
                            "beer.db").addCallback(roomDatabaseCallback).build()
                    Log.d(LOG_TAG, "Creating the database")
                }
            }
            return INSTANCE!!
        }

        private val roomDatabaseCallback = object : RoomDatabase.Callback() {

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                PopulateDbAsync(INSTANCE).execute()
            }
        }

        private class PopulateDbAsync(db: BeerDataBase?) : AsyncTask<Void, Void, Void>() {

            private val beerDao: BeerDao = db!!.beerDao()

            override fun doInBackground(vararg params: Void): Void? {
                // insert some dummy data
                var beer = Beer(name = "Beer1")
                beerDao.insertBeer(beer)
                beer = Beer(name = "Beer 2")
                beerDao.insertBeer(beer)
                return null
            }
        }
    }
}