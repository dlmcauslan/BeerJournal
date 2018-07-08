package com.wordpress.excelenteadventura.beerjournal.dependencyInjection

import com.wordpress.excelenteadventura.beerjournal.ui.addBeerActivity.AddBeerViewModelFactory
import com.wordpress.excelenteadventura.beerjournal.ui.mainActivity.MainViewModelFactory
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AddBeerViewModelModule::class, DatabaseModule::class])
interface AddBeerViewModelComponent {
    fun getAddBeerViewModelFactory() : AddBeerViewModelFactory
}