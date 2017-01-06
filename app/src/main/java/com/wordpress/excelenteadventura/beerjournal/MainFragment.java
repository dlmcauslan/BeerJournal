package com.wordpress.excelenteadventura.beerjournal;


import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wordpress.excelenteadventura.beerjournal.database.BeerContract.BeerEntry;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // Device dimensions for creating thumbnails
    public static int THUMB_SMALL_W;
    public static int THUMB_LARGE_W;

    // Identifier for the loader
    private static final int BEER_LOADER = 0;

    // Adapter for the listview
    BeerCursorAdapter mCursorAdapter;

    // Sort direction
    private static final boolean ASC = true;


    public MainFragment() {
        // Required empty public constructor

    }

    @Override
    public void onResume() {
        super.onResume();
        // This allows the list to be sorted if the sort type is changed.
        getLoaderManager().restartLoader(BEER_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Set thumbnail sizes
        int numPixW = getResources().getDisplayMetrics().widthPixels;
        int numPixH = getResources().getDisplayMetrics().heightPixels;
        int imageMax = Math.min(numPixH, numPixW);
        // Set size of small thumbnail to be phones smallest numPixels/5, large thumbnail is smallest
        // numPixels/2
        // TODO maybe save these as shared prefs.
        THUMB_SMALL_W = imageMax/5;
        THUMB_LARGE_W = imageMax/2;

        Log.v(LOG_TAG, "pixels: " + THUMB_SMALL_W + " " + THUMB_LARGE_W);

        final LoaderManager callbacks = getLoaderManager();

        // Inflate the layout for this fragment
        final View mFragmentView = inflater.inflate(R.layout.fragment_main, container, false);

        // This line enables the fragment to handle menu events
        setHasOptionsMenu(true);

        // Setup Floating Action Button to open next activity
        FloatingActionButton fab = (FloatingActionButton) mFragmentView.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    // Launches AddBeerActivity
                    Intent intent = new Intent(getActivity(), AddBeerActivity.class);;
                    // Start intent
                    startActivity(intent);
            }
        });

        // Find the listview to be populated with data
        ListView listView = (ListView) mFragmentView.findViewById(R.id.main_fragment_list_view);

        // Setup an adapter to create a list item for each row of beer data.
        // There will be no beer data until the loader finishes so pass in null for the cursor.
        mCursorAdapter = new BeerCursorAdapter(getActivity(), null);
        listView.setAdapter(mCursorAdapter);

        // Setup the list item on click listener. Jumps to editor activity.
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Create new intent to go to the AddBeer Activity
                Intent intent = new Intent(getActivity(), AddBeerActivity.class);

                // Create the content URI to pass through with the intent which represents the
                // Beer item that was clicked on.
                Uri currentBeerUri = ContentUris.withAppendedId(BeerEntry.CONTENT_URI,id);

                // Set the URI on the data field of the intent and launch the activity
                intent.setData(currentBeerUri);
                startActivity(intent);
            }
        });

        // Start the loader
        getLoaderManager().initLoader(BEER_LOADER, null, this);

        return mFragmentView;
    }


    // Options menu code
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Perform different activities based on which item the user clicks
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString(getString(R.string.pref_main_username), userName);
        editor.apply();
        switch(item.getItemId()) {
            // Click on ascending/descending menu item
            case R.id.action_asc_desc:
                // Change to opposite sortDirection
                Boolean sortDirection = prefs.getBoolean(getString(R.string.preference_sort_asc_desc), ASC);
                editor.putBoolean(getString(R.string.preference_sort_asc_desc), !sortDirection);
                editor.apply();
                // Reset loader manager to change sorting direction.
                getLoaderManager().restartLoader(BEER_LOADER, null, this);
                return true;
            // Click on sort_order menu item
            case R.id.action_sort_order:
                // Start intent to open sortOrder activity
                Intent intent = new Intent(getActivity(), SortOrderActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Projection specifying the columns we wish to load
        String[] projection = {BeerEntry._ID,
                                BeerEntry.COLUMN_BEER_NAME,
                                BeerEntry.COLUMN_BEER_TYPE,
                                BeerEntry.COLUMN_BEER_IBU,
                                BeerEntry.COLUMN_BREWERY_NAME,
                                BeerEntry.COLUMN_BREWERY_COUNTRY,
                                BeerEntry.COLUMN_BEER_DATE,
                                BeerEntry.COLUMN_BEER_PERCENTAGE,
                                BeerEntry.COLUMN_BEER_RATING,
                                BeerEntry.COLUMN_BEER_PHOTO};

        // Load the sort direction and type from sharedPrefs
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortType = prefs.getString(getString(R.string.preference_sort_order),getString(R.string.sort_beer_name));
        Boolean sortDirection = prefs.getBoolean(getString(R.string.preference_sort_asc_desc), ASC);

        String sortString = BeerEntry.COLUMN_BEER_NAME;
        if (sortType.equals(getString(R.string.sort_beer_name))) {
            if (sortDirection == ASC) sortString += " ASC";
            else sortString += " DESC";
        } else {
            if (sortType.equals(getString(R.string.sort_beer_type))) {
                sortString = BeerEntry.COLUMN_BEER_TYPE;
            } else if (sortType.equals(getString(R.string.sort_beer_percentage))) {
                sortString = BeerEntry.COLUMN_BEER_PERCENTAGE;
            } else if (sortType.equals(getString(R.string.sort_beer_rating))) {
                sortString = BeerEntry.COLUMN_BEER_RATING;
            } else if (sortType.equals(getString(R.string.sort_beer_date))) {
                sortString = BeerEntry.COLUMN_BEER_DATE;
            } else if (sortType.equals(getString(R.string.sort_beer_bitterness))) {
                sortString = BeerEntry.COLUMN_BEER_IBU;
            } else if (sortType.equals(getString(R.string.sort_beer_location))) {
                sortString = BeerEntry.COLUMN_BREWERY_COUNTRY;
                if (sortDirection == ASC) sortString += " ASC, ";
                else sortString += " DESC, ";
                sortString += BeerEntry.COLUMN_BREWERY_NAME;
            }

            if (sortDirection == ASC) sortString += " ASC, ";
            else sortString += " DESC, ";

            sortString += BeerEntry.COLUMN_BEER_NAME + " ASC";
        }

        Log.d(LOG_TAG, sortString);

        // Return the cursorLoader that will execute the content providers query method
        return new CursorLoader(getActivity(),
                                BeerEntry.CONTENT_URI,
                                projection,
                                null, null,
                                sortString); // TODO Add some logic to sort the data here.
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the cursorAdapter with updated data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
