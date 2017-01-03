package com.wordpress.excelenteadventura.beerjournal.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by DLMcAuslan on 12/31/2016.
 */

public final class BeerContract {

    // To prevent someone from accidentally instantiating the contract
    // give it an empty constructor
    private BeerContract() {}

    public static final String CONTENT_AUTHORITY = "com.wordpress.excelenteadventura.beerjournal";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_BEER = "beer";

    /**
     * Inner class that defines constant values for the beer database.
     * Each entry in the table represents an individual beer.
     */
    public static final class BeerEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BEER);

        // MIME type for list of beers
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_BEER;

        // MIME type for a single beer
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_BEER;

        // Database table name
        public static final String TABLE_NAME = "Beers";

        // Default values
        public static final String DEAULT_STRING = "Unknown";

        // Unique ID number for the beer in the database table
        // Type INTEGER
        public static final String _ID = BaseColumns._ID;

        // The table columns
        // Beer name - TEXT
        public static final String COLUMN_BEER_NAME = "Name";
        // Beer photo - BLOB
        public static final String COLUMN_BEER_PHOTO = "Photo";
        // Beer type - TEXT
        public static final String COLUMN_BEER_TYPE = "Type";
        // Beer rating - INTEGER (Divide by 2 to get actual rating out of 5)
        public static final String COLUMN_BEER_RATING = "Rating";
        // Beer percentage - REAL
        public static final String COLUMN_BEER_PERCENTAGE = "Percentage";
        // Beer bitterness - INTEGER
        public static final String COLUMN_BEER_IBU = "IBU";
        // Tasting date - TEXT
        public static final String COLUMN_BEER_DATE = "Date";
        // Comments - TEXT
        public static final String COLUMN_BEER_COMMENTS = "Comments";
        // Brewery name - TEXT
        public static final String COLUMN_BREWERY_NAME = "Brewery";
        // Brewery country - TEXT
        public static final String COLUMN_BREWERY_COUNTRY = "Country";
        // Brewery city - TEXT
        public static final String COLUMN_BREWERY_CITY = "City";
        // Brewery state/province - TEXT
        public static final String COLUMN_BREWERY_STATE = "State";


        // Checks whether rating is a valid value
        public static boolean isValidRating(int rating) {
            if (rating >=0 && rating <= 10) return true;
            return false;
        }

        // Checks whether beer percentage is a valid value
        public static boolean isValidPercentage(double percentage) {
            if (percentage >= -1 && percentage <= 100) return true;
            return false;
        }

        // Checks whether bitterness is a valid value
        public static boolean isValidBitterness(int bitterness) {
            if (bitterness >= -1) return true;
            return false;
        }

    }
}
