package com.wordpress.excelenteadventura.beerjournal.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

import com.wordpress.excelenteadventura.beerjournal.database.BeerContract.BeerEntry

/**
 * Created by DLMcAuslan on 1/1/2017.
 */

class BeerDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    /**
     * This is called when the database is first created.
     * @param db
     */
    override fun onCreate(db: SQLiteDatabase) {
        // Create a string that contains the SQL statement to create the beer table.
        val SQL_CREATE_BEER_TABLE = ("CREATE TABLE " + BeerEntry.TABLE_NAME + " ("
                + BeerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BeerEntry.COLUMN_BEER_NAME + " TEXT NOT NULL, "
                + BeerEntry.COLUMN_BEER_DATE + " TEXT NOT NULL, "
                + BeerEntry.COLUMN_BEER_PHOTO + " TEXT, "
                + BeerEntry.COLUMN_BEER_TYPE + " TEXT NOT NULL DEFAULT 'Unknown', "
                + BeerEntry.COLUMN_BEER_RATING + " INTEGER NOT NULL DEFAULT 0, "
                + BeerEntry.COLUMN_BEER_IBU + " INTEGER NOT NULL DEFAULT -1, "
                + BeerEntry.COLUMN_BEER_PERCENTAGE + " REAL NOT NULL DEFAULT -1, "
                + BeerEntry.COLUMN_BREWERY_NAME + " TEXT NOT NULL DEFAULT 'Unknown', "
                + BeerEntry.COLUMN_BREWERY_CITY + " TEXT NOT NULL DEFAULT 'Unknown', "
                + BeerEntry.COLUMN_BREWERY_STATE + " TEXT,"
                + BeerEntry.COLUMN_BREWERY_COUNTRY + " TEXT NOT NULL DEFAULT 'Unknown', "
                + BeerEntry.COLUMN_BEER_COMMENTS + " TEXT);")

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_BEER_TABLE)
        Log.i(LOG_TAG, BeerEntry.TABLE_NAME + " created.")
    }

    /**
     * This is called when the database needs to be upgraded.
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // The database is still at version 1, so there's nothing to be done here yet
    }

    companion object {

        private val LOG_TAG = BeerDbHelper::class.java.simpleName

        private val DATABASE_NAME = "BeerJournal"
        private val DATABASE_VERSION = 1
    }
}
