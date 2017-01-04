package com.wordpress.excelenteadventura.beerjournal;


import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
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

    // Identifier for the loader
    private static final int BEER_LOADER = 0;

    // Adapter for the listview
    BeerCursorAdapter mCursorAdapter;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mFragmentView = inflater.inflate(R.layout.fragment_main, container, false);

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

        // Return the cursorLoader that will execute the content providers query method
        return new CursorLoader(getActivity(),
                                BeerEntry.CONTENT_URI,
                                projection,
                                null, null, null); // TODO Add some logic to sort the data here.
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
