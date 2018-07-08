package com.wordpress.excelenteadventura.beerjournal.ui.mainActivity

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.wordpress.excelenteadventura.beerjournal.BeerRepository
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("UNCHECKED_CAST")
/**
 * Factory method that allows us to create a ViewModel with a constructor that takes a
 * [BeerRepository]
 */
@Singleton
class MainViewModelFactory @Inject constructor(private val repository: BeerRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return MainViewModel(repository) as T
    }
}