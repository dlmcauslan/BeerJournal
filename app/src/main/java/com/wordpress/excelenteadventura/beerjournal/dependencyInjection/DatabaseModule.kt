package com.wordpress.excelenteadventura.beerjournal.dependencyInjection

import android.arch.persistence.room.Room
import android.content.Context
import com.wordpress.excelenteadventura.beerjournal.BeerRepository
import com.wordpress.excelenteadventura.beerjournal.database.BeerDao
import com.wordpress.excelenteadventura.beerjournal.database.BeerDataBase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule(val context: Context) {

    @Provides
    @Singleton
    fun providesBeerDatabase(): BeerDataBase {
        return Room.databaseBuilder(context.applicationContext,
                    BeerDataBase::class.java,
                    "beer.db").build()
    }

    @Provides
    @Singleton
    fun providesBeerDao(database: BeerDataBase) = database.beerDao()

    @Provides
    @Singleton
    fun providesBeerRepository(beerDao: BeerDao) = BeerRepository(beerDao)
}