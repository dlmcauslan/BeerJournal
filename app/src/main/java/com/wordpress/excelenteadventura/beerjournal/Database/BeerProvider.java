package com.wordpress.excelenteadventura.beerjournal.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.wordpress.excelenteadventura.beerjournal.database.BeerContract.BeerEntry;

/**
 * Content provider for Beer Journal app.
 * Created by DLMcAuslan on 1/1/2017.
 */

public class BeerProvider extends ContentProvider {

    private static final String LOG_TAG = BeerProvider.class.getSimpleName();

    // URI matcher codes for whole table (100) and single beer (101)
    private static final int BEERS = 100;
    private static final int BEER_ID = 101;

    // URI matcher object
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class
    static {
        // Content URI to provide access to multiple rows of the Beers table.
        sUriMatcher.addURI(BeerContract.CONTENT_AUTHORITY, BeerContract.PATH_BEER, BEERS);
        // Content URI to provide access to a single row of the Beers table.
        // # is used as the wildcard character that is substituted as an integer number to
        // access the nth row of the table.
        sUriMatcher.addURI(BeerContract.CONTENT_AUTHORITY, BeerContract.PATH_BEER + "/#", BEER_ID);
    }

    // Database helper object
    private BeerDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new BeerDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // The cursor that holds the result of the query
        Cursor cursor;

        // Decide which type of database query we're doing
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BEERS:
                cursor = database.query(BeerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case BEER_ID:
                selection = BeerEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(BeerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the cursor so we know what content URI the
        // cursor was created for. If the data at this URI changes, we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BEERS:
                return insertBeer(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    // Insert a beer into the database. Return the new contentURI for the added row in the database
    private Uri insertBeer(Uri uri, ContentValues values) {
        // Check that values to insert are valid
        String name = values.getAsString(BeerEntry.COLUMN_BEER_NAME);
        String date = values.getAsString(BeerEntry.COLUMN_BEER_DATE);
//        String breweryName = values.getAsString(BeerEntry.COLUMN_BREWERY_NAME);
//        String city = values.getAsString(BeerEntry.COLUMN_BREWERY_CITY);
//        String country = values.getAsString(BeerEntry.COLUMN_BREWERY_COUNTRY);
        Integer rating = values.getAsInteger(BeerEntry.COLUMN_BEER_RATING);
        Double percentage = values.getAsDouble(BeerEntry.COLUMN_BEER_PERCENTAGE);
        Integer ibu = values.getAsInteger(BeerEntry.COLUMN_BEER_IBU);
        if (name == null) throw new IllegalArgumentException("Beer requires a name.");
        if (date == null) throw new IllegalArgumentException("Tasting date required.");
//        if (city == null) throw new Illeg"Tasting date required.");
//        if (breweryName == null) throw alArgumentException("City required.");
//        if (country == null) throw new IllegalArgumentException("Country required.");
        if (rating != null && !BeerEntry.isValidRating(rating)) throw new IllegalArgumentException("Beer rating must be between 0 and 5.");
        if (percentage != null && !BeerEntry.isValidPercentage(percentage)) throw new IllegalArgumentException("Percentage must be between -1 and 100");
        if (ibu != null && !BeerEntry.isValidBitterness(ibu)) throw new IllegalArgumentException("Beer bitterness must be greater than -1.");

        // Don't need to check the other fields, all values are valid, including null.

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert a new beer into the database
        long id = database.insert(BeerEntry.TABLE_NAME, null, values);
        // If the id is -1, the insertion failed, so log the error and return null
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for beer content uri
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID of the newly iserted row
        return ContentUris.withAppendedId(uri, id);

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BEERS:
                return updateBeer(uri, values, selection, selectionArgs);
            case BEER_ID:
                selection = BeerEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateBeer(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    // Update a beer in the database with the given content values. Return the number
    // of rows that were successfully updated.
    private int updateBeer(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Check that the values to update are valid.
        if (values.containsKey(BeerEntry.COLUMN_BEER_NAME)) {
            String name = values.getAsString(BeerEntry.COLUMN_BEER_NAME);
            if (name == null) throw new IllegalArgumentException("Beer requires a name.");
        }
        if (values.containsKey(BeerEntry.COLUMN_BEER_DATE)) {
            String date = values.getAsString(BeerEntry.COLUMN_BEER_DATE);
            if (date == null) throw new IllegalArgumentException("Tasting date required.");
        }
        if (values.containsKey(BeerEntry.COLUMN_BEER_RATING)) {
            Integer rating = values.getAsInteger(BeerEntry.COLUMN_BEER_RATING);
            if (rating != null && !BeerEntry.isValidRating(rating)) throw new IllegalArgumentException("Beer rating must be between 0 and 5.");
        }
        if (values.containsKey(BeerEntry.COLUMN_BEER_PERCENTAGE)) {
            Double percentage = values.getAsDouble(BeerEntry.COLUMN_BEER_PERCENTAGE);
            if (percentage != null && !BeerEntry.isValidPercentage(percentage)) throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }
        if (values.containsKey(BeerEntry.COLUMN_BEER_IBU)) {
            Integer ibu = values.getAsInteger(BeerEntry.COLUMN_BEER_IBU);
            if (ibu != null && !BeerEntry.isValidBitterness(ibu)) throw new IllegalArgumentException("Beer bitterness must be greater than 0.");
        }

        // No need to check the other columns because any value is valid.

        // Don't update the database if there are no values to update.
        if (values.size() == 0) return 0;

        // Otherwise get writeable database and perform the update
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(BeerEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, notify all the listeners that the data at the uri has changed
        if (rowsUpdated != 0) getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }



    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get a writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BEERS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(BeerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BEER_ID:
                // Delete the row given by the ID in the uri
                selection = BeerEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BeerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows have been deleted, notify the listeners that the data has been changed
        if (rowsDeleted != 0) getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BEERS:
                return BeerEntry.CONTENT_LIST_TYPE;
            case BEER_ID:
                return BeerEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }
}
