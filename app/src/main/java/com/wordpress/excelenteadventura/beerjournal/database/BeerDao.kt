package com.wordpress.excelenteadventura.beerjournal.database

import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update

interface BeerDao {

    @Query("select * from beer_table")
    fun getAllBeers(): List<Beer>

    @Query("select * from beer_table where id = :id")
    fun getBeerById(id: Long): Beer

    @Insert(onConflict = REPLACE)
    fun insertBeer(beer: Beer)

    @Update(onConflict = REPLACE)
    fun updateBeer(beer: Beer)

    @Delete
    fun deleteBeer(beer: Beer)
}