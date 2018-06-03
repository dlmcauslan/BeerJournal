package com.wordpress.excelenteadventura.beerjournal.ui.mainActivity

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.wordpress.excelenteadventura.beerjournal.BeerRepository
import com.wordpress.excelenteadventura.beerjournal.database.Beer

class MainViewModel(repository: BeerRepository): ViewModel() {

    private val beers: LiveData<List<Beer>> = repository.getAllBeers()

    // TODO: is this function necessary or make beers public
    // Also var or val for livedata...
    fun getAllBeers(): LiveData<List<Beer>> {
        return beers
    }
}