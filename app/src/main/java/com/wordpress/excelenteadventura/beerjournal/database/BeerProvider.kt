package com.wordpress.excelenteadventura.beerjournal.database

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.wordpress.excelenteadventura.beerjournal.database.BeerContract.BeerEntry

/**
 * Content provider for Beer Journal app.
 * Created by DLMcAuslan on 1/1/2017.
 */

class BeerProvider : ContentProvider() {

    // Database helper object
    private var mDbHelper: BeerDbHelper? = null

    override fun onCreate(): Boolean {
        mDbHelper = BeerDbHelper(context!!)
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        var selection = selection
        var selectionArgs = selectionArgs
        // Get readable database
        val database = mDbHelper!!.readableDatabase

        // The cursor that holds the result of the query
        val cursor: Cursor

        // Decide which type of database query we're doing
        val match = sUriMatcher.match(uri)
        when (match) {
            BEERS -> cursor = database.query(BeerEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs, null, null,
                    sortOrder)
            BEER_ID -> {
                selection = BeerEntry._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                cursor = database.query(BeerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs, null, null,
                        sortOrder)
            }
            else -> throw IllegalArgumentException("Cannot query unknown URI $uri")
        }

        // Set notification URI on the cursor so we know what content URI the
        // cursor was created for. If the data at this URI changes, we know we need to update the Cursor.
        cursor.setNotificationUri(context!!.contentResolver, uri)

        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val match = sUriMatcher.match(uri)
        when (match) {
            BEERS -> return insertBeer(uri, values!!)
            else -> throw IllegalArgumentException("Insertion is not supported for $uri")
        }
    }

    // Insert a beer into the database. Return the new contentURI for the added row in the database
    private fun insertBeer(uri: Uri, values: ContentValues): Uri? {
        // Check that values to insert are valid
        val name = values.getAsString(BeerEntry.COLUMN_BEER_NAME)
        val date = values.getAsString(BeerEntry.COLUMN_BEER_DATE)
        //        String breweryName = values.getAsString(BeerEntry.COLUMN_BREWERY_NAME);
        //        String city = values.getAsString(BeerEntry.COLUMN_BREWERY_CITY);
        //        String country = values.getAsString(BeerEntry.COLUMN_BREWERY_COUNTRY);
        val rating = values.getAsInteger(BeerEntry.COLUMN_BEER_RATING)
        val percentage = values.getAsDouble(BeerEntry.COLUMN_BEER_PERCENTAGE)
        val ibu = values.getAsInteger(BeerEntry.COLUMN_BEER_IBU)
        if (name == null) throw IllegalArgumentException("Beer requires a name.")
        if (date == null) throw IllegalArgumentException("Tasting date required.")
        //        if (city == null) throw new Illeg"Tasting date required.");
        //        if (breweryName == null) throw alArgumentException("City required.");
        //        if (country == null) throw new IllegalArgumentException("Country required.");
        if (rating != null && !BeerEntry.isValidRating(rating)) throw IllegalArgumentException("Beer rating must be between 0 and 5.")
        if (percentage != null && !BeerEntry.isValidPercentage(percentage)) throw IllegalArgumentException("Percentage must be between -1 and 100")
        if (ibu != null && !BeerEntry.isValidBitterness(ibu)) throw IllegalArgumentException("Beer bitterness must be greater than -1.")

        // Don't need to check the other fields, all values are valid, including null.

        // Get writeable database
        val database = mDbHelper!!.writableDatabase

        // Insert a new beer into the database
        val id = database.insert(BeerEntry.TABLE_NAME, null, values)
        // If the id is -1, the insertion failed, so log the error and return null
        if (id == -1L) {
            Log.e(LOG_TAG, "Failed to insert row for $uri")
            return null
        }

        // Notify all listeners that the data has changed for beer content uri
        context!!.contentResolver.notifyChange(uri, null)

        // Return the new URI with the ID of the newly iserted row
        return ContentUris.withAppendedId(uri, id)

    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        var selection = selection
        var selectionArgs = selectionArgs
        val match = sUriMatcher.match(uri)
        when (match) {
            BEERS -> return updateBeer(uri, values!!, selection, selectionArgs)
            BEER_ID -> {
                selection = BeerEntry._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                return updateBeer(uri, values!!, selection, selectionArgs)
            }
            else -> throw IllegalArgumentException("Update is not supported for $uri")
        }
    }

    // Update a beer in the database with the given content values. Return the number
    // of rows that were successfully updated.
    private fun updateBeer(uri: Uri, values: ContentValues, selection: String?, selectionArgs: Array<String>?): Int {
        // Check that the values to update are valid.
        if (values.containsKey(BeerEntry.COLUMN_BEER_NAME)) {
            val name = values.getAsString(BeerEntry.COLUMN_BEER_NAME)
                    ?: throw IllegalArgumentException("Beer requires a name.")
        }
        if (values.containsKey(BeerEntry.COLUMN_BEER_DATE)) {
            val date = values.getAsString(BeerEntry.COLUMN_BEER_DATE)
                    ?: throw IllegalArgumentException("Tasting date required.")
        }
        if (values.containsKey(BeerEntry.COLUMN_BEER_RATING)) {
            val rating = values.getAsInteger(BeerEntry.COLUMN_BEER_RATING)
            if (rating != null && !BeerEntry.isValidRating(rating)) throw IllegalArgumentException("Beer rating must be between 0 and 5.")
        }
        if (values.containsKey(BeerEntry.COLUMN_BEER_PERCENTAGE)) {
            val percentage = values.getAsDouble(BeerEntry.COLUMN_BEER_PERCENTAGE)
            if (percentage != null && !BeerEntry.isValidPercentage(percentage)) throw IllegalArgumentException("Percentage must be between 0 and 100")
        }
        if (values.containsKey(BeerEntry.COLUMN_BEER_IBU)) {
            val ibu = values.getAsInteger(BeerEntry.COLUMN_BEER_IBU)
            if (ibu != null && !BeerEntry.isValidBitterness(ibu)) throw IllegalArgumentException("Beer bitterness must be greater than 0.")
        }

        // No need to check the other columns because any value is valid.

        // Don't update the database if there are no values to update.
        if (values.size() == 0) return 0

        // Otherwise get writeable database and perform the update
        val database = mDbHelper!!.writableDatabase
        val rowsUpdated = database.update(BeerEntry.TABLE_NAME, values, selection, selectionArgs)

        // If 1 or more rows were updated, notify all the listeners that the data at the uri has changed
        if (rowsUpdated != 0) context!!.contentResolver.notifyChange(uri, null)

        return rowsUpdated
    }


    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        var selection = selection
        var selectionArgs = selectionArgs
        // Get a writeable database
        val database = mDbHelper!!.writableDatabase

        val rowsDeleted: Int

        val match = sUriMatcher.match(uri)
        when (match) {
            BEERS ->
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(BeerEntry.TABLE_NAME, selection, selectionArgs)
            BEER_ID -> {
                // Delete the row given by the ID in the uri
                selection = BeerEntry._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                rowsDeleted = database.delete(BeerEntry.TABLE_NAME, selection, selectionArgs)
            }
            else -> throw IllegalArgumentException("Deletion is not supported for $uri")
        }

        // If 1 or more rows have been deleted, notify the listeners that the data has been changed
        if (rowsDeleted != 0) context!!.contentResolver.notifyChange(uri, null)
        return rowsDeleted
    }

    override fun getType(uri: Uri): String? {
        val match = sUriMatcher.match(uri)
        when (match) {
            BEERS -> return BeerEntry.CONTENT_LIST_TYPE
            BEER_ID -> return BeerEntry.CONTENT_ITEM_TYPE
            else -> throw IllegalArgumentException("Unknown URI $uri with match $match")
        }
    }

    companion object {

        private val LOG_TAG = BeerProvider::class.java.simpleName

        // URI matcher codes for whole table (100) and single beer (101)
        private val BEERS = 100
        private val BEER_ID = 101

        // URI matcher object
        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        // Static initializer. This is run the first time anything is called from this class
        init {
            // Content URI to provide access to multiple rows of the Beers table.
            sUriMatcher.addURI(BeerContract.CONTENT_AUTHORITY, BeerContract.PATH_BEER, BEERS)
            // Content URI to provide access to a single row of the Beers table.
            // # is used as the wildcard character that is substituted as an integer number to
            // access the nth row of the table.
            sUriMatcher.addURI(BeerContract.CONTENT_AUTHORITY, BeerContract.PATH_BEER + "/#", BEER_ID)
        }
    }
}
