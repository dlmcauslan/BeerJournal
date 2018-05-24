package com.wordpress.excelenteadventura.beerjournal.database

import android.content.ContentResolver
import android.net.Uri
import android.provider.BaseColumns

/**
 * Created by DLMcAuslan on 12/31/2016.
 */

object BeerContract {

    val CONTENT_AUTHORITY = "com.wordpress.excelenteadventura.beerjournal"

    val BASE_CONTENT_URI = Uri.parse("content://$CONTENT_AUTHORITY")

    val PATH_BEER = "beer"

    /**
     * Inner class that defines constant values for the beer database.
     * Each entry in the table represents an individual beer.
     */
    class BeerEntry : BaseColumns {
        companion object {

            val CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BEER)

            // MIME type for list of beers
            val CONTENT_LIST_TYPE = (ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                    + CONTENT_AUTHORITY + "/" + PATH_BEER)

            // MIME type for a single beer
            val CONTENT_ITEM_TYPE = (ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                    + CONTENT_AUTHORITY + "/" + PATH_BEER)

            // Database table name
            val TABLE_NAME = "Beers"

            // Default values
            val DEAULT_STRING = "Unknown"

            // Unique ID number for the beer in the database table
            // Type INTEGER
            val _ID = BaseColumns._ID

            // The table columns
            // Beer name - TEXT
            val COLUMN_BEER_NAME = "Name"
            // Beer photo - String of file locations.
            val COLUMN_BEER_PHOTO = "Photo"
            // Beer type - TEXT
            val COLUMN_BEER_TYPE = "Type"
            // Beer rating - INTEGER (Divide by 2 to get actual rating out of 5)
            val COLUMN_BEER_RATING = "Rating"
            // Beer percentage - REAL
            val COLUMN_BEER_PERCENTAGE = "Percentage"
            // Beer bitterness - INTEGER
            val COLUMN_BEER_IBU = "IBU"
            // Tasting date - TEXT
            val COLUMN_BEER_DATE = "Date"
            // Comments - TEXT
            val COLUMN_BEER_COMMENTS = "Comments"
            // Brewery name - TEXT
            val COLUMN_BREWERY_NAME = "Brewery"
            // Brewery country - TEXT
            val COLUMN_BREWERY_COUNTRY = "Country"
            // Brewery city - TEXT
            val COLUMN_BREWERY_CITY = "City"
            // Brewery state/province - TEXT
            val COLUMN_BREWERY_STATE = "State"


            // Checks whether rating is a valid value
            fun isValidRating(rating: Int): Boolean {
                return if (rating >= 0 && rating <= 10) true else false
            }

            // Checks whether beer percentage is a valid value
            fun isValidPercentage(percentage: Double): Boolean {
                return if (percentage >= -1 && percentage <= 100) true else false
            }

            // Checks whether bitterness is a valid value
            fun isValidBitterness(bitterness: Int): Boolean {
                return if (bitterness >= -1) true else false
            }
        }

    }
}// To prevent someone from accidentally instantiating the contract
// give it an empty constructor
