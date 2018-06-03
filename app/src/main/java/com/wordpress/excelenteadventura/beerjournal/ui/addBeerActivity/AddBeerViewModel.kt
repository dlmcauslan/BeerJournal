package com.wordpress.excelenteadventura.beerjournal.ui.addBeerActivity

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.wordpress.excelenteadventura.beerjournal.BeerRepository
import com.wordpress.excelenteadventura.beerjournal.database.Beer

class AddBeerViewModel(private val repository: BeerRepository): ViewModel() {

    fun insertBeer(beer: Beer) {
        repository.insertBeer(beer)
    }

    fun updateBeer(beer: Beer) {
        repository.updateBeer(beer)
    }

    val currentBeer: LiveData<Beer?> = repository.currentBeer
}