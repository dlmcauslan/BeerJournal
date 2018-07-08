package com.wordpress.excelenteadventura.beerjournal.ui.mainActivity

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.wordpress.excelenteadventura.beerjournal.BeerRepository
import com.wordpress.excelenteadventura.beerjournal.database.Beer

class MainViewModel(private val repository: BeerRepository): ViewModel() {

    val beers: LiveData<List<Beer>> = repository.beers

    fun setCurrentBeer(beer: Beer?) {
       repository.currentBeer.value = beer
    }
}