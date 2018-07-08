package com.wordpress.excelenteadventura.beerjournal.dependencyInjection

import com.wordpress.excelenteadventura.beerjournal.ui.addBeerActivity.AddBeerViewModelFactory
import com.wordpress.excelenteadventura.beerjournal.ui.mainActivity.MainViewModelFactory
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [MainViewModelModule::class, DatabaseModule::class])
interface MainViewModelComponent {
    fun getMainViewModelFactory() : MainViewModelFactory
}