package com.wordpress.excelenteadventura.beerjournal.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "beer_table")
data class Beer(
        @PrimaryKey(autoGenerate = true) var id: Long? = null,
        @ColumnInfo(name = "name") var name: String = "",
        @ColumnInfo(name = "photo_location") var photoLocation: String = "",
        @ColumnInfo(name = "type") var type: String = "",
        @ColumnInfo(name = "rating") var rating: Int = 0,
        @ColumnInfo(name = "percentage") var percentage: Double = 0.0,
        @ColumnInfo(name = "bitterness") var bitterness: Int = 0,
        @ColumnInfo(name = "date") var date: String = "",
        @ColumnInfo(name = "comments") var comments: String = "",
        @ColumnInfo(name = "brewery") var brewery: String = "",
        @ColumnInfo(name = "country") var country: String = "",
        @ColumnInfo(name = "city") var city: String = "",
        @ColumnInfo(name = "state") var state: String = ""
)
