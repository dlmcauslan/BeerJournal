package com.wordpress.excelenteadventura.beerjournal

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.os.AsyncTask
import android.util.Log
import com.wordpress.excelenteadventura.beerjournal.database.Beer
import com.wordpress.excelenteadventura.beerjournal.database.BeerDao

/**
 * Repository class to provide an abstraction between the database and
 * the UI
 */
class BeerRepository private constructor(private val beerDao: BeerDao) {

    val beers: LiveData<List<Beer>> = beerDao.getAllBeers()
    val currentBeer: MutableLiveData<Beer?> = MutableLiveData()

    fun insertBeer(beer: Beer) {
        InsertAsyncTask(beerDao).execute(beer)
    }

    fun updateBeer(beer: Beer) {
        UpdateAsyncTask(beerDao).execute(beer)
    }

    fun deleteBeer(beer: Beer) {
        DeleteAsyncTask(beerDao).execute(beer)
    }

    private class InsertAsyncTask internal constructor(private val asyncTaskDao: BeerDao) : AsyncTask<Beer, Void, Void>() {
        override fun doInBackground(vararg params: Beer): Void? {
            asyncTaskDao.insertBeer(params[0])
            return null
        }
    }

    private class UpdateAsyncTask internal constructor(private val asyncTaskDao: BeerDao) : AsyncTask<Beer, Void, Void>() {
        override fun doInBackground(vararg params: Beer): Void? {
            asyncTaskDao.updateBeer(params[0])
            return null
        }
    }

    private class DeleteAsyncTask internal constructor(private val asyncTaskDao: BeerDao) : AsyncTask<Beer, Void, Void>() {
        override fun doInBackground(vararg params: Beer): Void? {
            asyncTaskDao.deleteBeer(params[0])
            return null
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