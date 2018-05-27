package com.wordpress.excelenteadventura.beerjournal

import android.app.Application
import android.arch.lifecycle.LiveData
import android.os.AsyncTask
import com.wordpress.excelenteadventura.beerjournal.database.Beer
import com.wordpress.excelenteadventura.beerjournal.database.BeerDao
import com.wordpress.excelenteadventura.beerjournal.database.BeerDataBase

/**
 * Repository class to provide an abstraction between the database and
 * the UI
 */
class BeerRepository(application: Application) {
    private val beerDao: BeerDao
    private val beers: LiveData<List<Beer>>

    init {
        val db = BeerDataBase.getDatabase(application)
        beerDao = db.beerDao()
        beers = beerDao.getAllBeers()
    }

    // TODO: is this necessary or just make beers public
    fun getAllBeers(): LiveData<List<Beer>> {
        return beers
    }

    fun insertBeer(beer: Beer) {
        InsertAsyncTask(beerDao).execute(beer)
    }

    private class InsertAsyncTask internal constructor(private val asyncTaskDao: BeerDao) : AsyncTask<Beer, Void, Void>() {
        override fun doInBackground(vararg params: Beer): Void? {
            asyncTaskDao.insertBeer(params[0])
            return null
        }
    }
}