package com.wordpress.excelenteadventura.beerjournal

import android.content.Context
import com.wordpress.excelenteadventura.beerjournal.database.BeerDataBase
import com.wordpress.excelenteadventura.beerjournal.ui.addBeerActivity.AddBeerViewModelFactory
import com.wordpress.excelenteadventura.beerjournal.ui.mainActivity.MainViewModelFactory

/**
 * Provides static methods to inject the various classes needed for Sunshine
 */
object InjectorUtils {

    private fun provideRepository(context: Context): BeerRepository {
        val database =  BeerDataBase.getDatabase(context.applicationContext)
        return BeerRepository.getInstance(database.beerDao())
    }

    fun provideAddBeerViewModelFactory(context: Context): AddBeerViewModelFactory {
        val repository = provideRepository(context.applicationContext)
        return AddBeerViewModelFactory(repository)
    }

    fun provideMainActivityViewModelFactory(context: Context): MainViewModelFactory {
        val repository = provideRepository(context.applicationContext)
        return MainViewModelFactory(repository)
    }

}