package com.wordpress.excelenteadventura.beerjournal.dependencyInjection

import android.arch.persistence.room.Room
import android.content.Context
import com.wordpress.excelenteadventura.beerjournal.BeerRepository
import com.wordpress.excelenteadventura.beerjournal.database.BeerDao
import com.wordpress.excelenteadventura.beerjournal.database.BeerDataBase
import com.wordpress.excelenteadventura.beerjournal.ui.addBeerActivity.AddBeerViewModelFactory
import com.wordpress.excelenteadventura.beerjournal.ui.mainActivity.MainViewModelFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AddBeerViewModelModule {

    @Provides
    @Singleton
    fun providesAddBeerViewModel(repository: BeerRepository) = AddBeerViewModelFactory(repository)
}