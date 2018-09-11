package com.wordpress.excelenteadventura.beerjournal.ui.addBeerActivity

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.wordpress.excelenteadventura.beerjournal.BeerRepository

/**
 * Factory method that allows us to create a ViewModel with a constructor that takes a
 * [BeerRepository]
 */
class AddBeerViewModelFactory(private val repository: BeerRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddBeerViewModel(repository) as T
    }
}