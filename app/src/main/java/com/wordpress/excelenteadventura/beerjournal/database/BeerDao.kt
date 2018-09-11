package com.wordpress.excelenteadventura.beerjournal.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE

@Dao
interface BeerDao {

    @Query("select * from beer_table")
    fun getAllBeers(): LiveData<List<Beer>>

    @Query("select * from beer_table where id = :id")
    fun getBeerById(id: Long): Beer

    @Insert(onConflict = REPLACE)
    fun insertBeer(beer: Beer)

    @Update(onConflict = REPLACE)
    fun updateBeer(beer: Beer)

    @Delete
    fun deleteBeer(beer: Beer)
}