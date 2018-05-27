package com.wordpress.excelenteadventura.beerjournal.mainActivity

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.wordpress.excelenteadventura.beerjournal.BeerRepository
import com.wordpress.excelenteadventura.beerjournal.database.Beer

class MainActivityViewModel(application: Application): AndroidViewModel(application) {

    private val repository: BeerRepository = BeerRepository(application)
    private val beers: LiveData<List<Beer>> = repository.getAllBeers()

    // TODO: is this function necessary or make beers public
    // Also var or val for livedata...
    fun getAllBeers(): LiveData<List<Beer>> {
        return beers
    }

    fun insertBeer(beer: Beer) {
        repository.insertBeer(beer)
    }
}