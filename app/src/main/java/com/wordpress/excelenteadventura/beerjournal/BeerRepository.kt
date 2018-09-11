package com.wordpress.excelenteadventura.beerjournal

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.wordpress.excelenteadventura.beerjournal.database.Beer
import com.wordpress.excelenteadventura.beerjournal.database.BeerDao
import kotlinx.coroutines.experimental.launch

/**
 * Repository class to provide an abstraction between the database and
 * the UI
 */
class BeerRepository private constructor(private val beerDao: BeerDao) {

    val beers: LiveData<List<Beer>> = beerDao.getAllBeers()
    val currentBeer = MutableLiveData<Beer?>()

    fun insertBeer(beer: Beer) {
        launch {
            beerDao.insertBeer(beer)
        }
    }

    fun updateBeer(beer: Beer) {
        launch {
            beerDao.updateBeer(beer)
        }
    }

    fun deleteBeer(beer: Beer) {
        launch {
            beerDao.deleteBeer(beer)
        }
    }

    companion object {
        private val LOG_TAG = BeerRepository::class.java.simpleName

        // For singleton instantiation
        private val LOCK = Any()
        private var instance: BeerRepository? = null

        fun getInstance(beerDao: BeerDao): BeerRepository {
            Log.d(LOG_TAG, "Getting the repository")
            if (instance == null) {
                synchronized(LOCK) {
                    instance = BeerRepository(beerDao)
                    Log.d(LOG_TAG, "Made new repository")
                }
            }
            return instance!!
        }
    }
}