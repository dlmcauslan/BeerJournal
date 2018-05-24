package com.wordpress.excelenteadventura.beerjournal


import android.content.ContentUris
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ListView
import com.wordpress.excelenteadventura.beerjournal.database.BeerContract.BeerEntry


/**
 * A simple [Fragment] subclass.
 */
class MainFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {

    // Adapter for the listview
    lateinit var cursorAdapter: BeerCursorAdapter

    override fun onResume() {
        super.onResume()
        // This allows the list to be sorted if the sort type is changed.
        loaderManager.restartLoader(BEER_LOADER, null, this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Set thumbnail sizes
        val numPixW = resources.displayMetrics.widthPixels
        val numPixH = resources.displayMetrics.heightPixels
        val imageMax = Math.min(numPixH, numPixW)
        // Set size of small thumbnail to be phones smallest numPixels/5, large thumbnail is smallest
        // numPixels/2
        // TODO maybe save these as shared prefs.
        THUMB_SMALL_W = imageMax / 5
        THUMB_LARGE_W = imageMax / 2

        Log.v(LOG_TAG, "pixels: $THUMB_SMALL_W $THUMB_LARGE_W")

//        val callbacks = loaderManager

        // Inflate the layout for this fragment
        val fragmentView = inflater.inflate(R.layout.fragment_main, container, false)

        // This line enables the fragment to handle menu events
        setHasOptionsMenu(true)

        // Setup Floating Action Button to open next activity
        val fab = fragmentView.findViewById(R.id.floatingActionButton) as FloatingActionButton
        fab.setOnClickListener {
            // Launches AddBeerActivity
            val intent = Intent(activity, AddBeerActivity::class.java)
            // Start intent
            startActivity(intent)
        }

        // Find the listview to be populated with data
        val listView = fragmentView.findViewById(R.id.main_fragment_list_view) as ListView

        // Setup an adapter to create a list item for each row of beer data.
        // There will be no beer data until the loader finishes so pass in null for the cursor.
        cursorAdapter = BeerCursorAdapter(activity, null)
        listView.adapter = cursorAdapter

        // Setup the list item on click listener. Jumps to editor activity.
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, _, id ->
            // Create new intent to go to the AddBeer Activity
            val intent = Intent(activity, AddBeerActivity::class.java)

            // Create the content URI to pass through with the intent which represents the
            // Beer item that was clicked on.
            val currentBeerUri = ContentUris.withAppendedId(BeerEntry.CONTENT_URI, id)

            // Set the URI on the data field of the intent and launch the activity
            intent.data = currentBeerUri
            startActivity(intent)
        }

        // Start the loader
        loaderManager.initLoader(BEER_LOADER, null, this)

        return fragmentView
    }


    // Options menu code
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        activity.menuInflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // Perform different activities based on which item the user clicks
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = prefs.edit()
        //        editor.putString(getString(R.string.pref_main_username), userName);
        editor.apply()
        when (item!!.itemId) {
        // Click on ascending/descending menu item
            R.id.action_asc_desc -> {
                // Change to opposite sortDirection
                val sortDirection = prefs.getBoolean(getString(R.string.preference_sort_asc_desc), ASC)
                editor.putBoolean(getString(R.string.preference_sort_asc_desc), !sortDirection)
                editor.apply()
                // Reset loader manager to change sorting direction.
                loaderManager.restartLoader(BEER_LOADER, null, this)
                return true
            }
        // Click on sort_order menu item
            R.id.action_sort_order -> {
                // Start intent to open sortOrder activity
                val intent = Intent(activity, SortOrderActivity::class.java)
                startActivity(intent)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateLoader(id: Int, args: Bundle): Loader<Cursor> {
        // Projection specifying the columns we wish to load
        val projection = arrayOf(BeerEntry._ID,
                BeerEntry.COLUMN_BEER_NAME,
                BeerEntry.COLUMN_BEER_TYPE,
                BeerEntry.COLUMN_BEER_IBU,
                BeerEntry.COLUMN_BREWERY_NAME,
                BeerEntry.COLUMN_BREWERY_COUNTRY,
                BeerEntry.COLUMN_BEER_DATE,
                BeerEntry.COLUMN_BEER_PERCENTAGE,
                BeerEntry.COLUMN_BEER_RATING,
                BeerEntry.COLUMN_BEER_PHOTO)

        // Load the sort direction and type from sharedPrefs
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        val sortType = prefs.getString(getString(R.string.preference_sort_order), getString(R.string.sort_beer_name))
        val sortDirection = prefs.getBoolean(getString(R.string.preference_sort_asc_desc), ASC)

        var sortString = BeerEntry.COLUMN_BEER_NAME
        if (sortType == getString(R.string.sort_beer_name)) {
            sortString += if (sortDirection == ASC) " ASC" else " DESC"
        } else {
            when (sortType) {
                getString(R.string.sort_beer_type) -> sortString = BeerEntry.COLUMN_BEER_TYPE
                getString(R.string.sort_beer_percentage) -> sortString = BeerEntry.COLUMN_BEER_PERCENTAGE
                getString(R.string.sort_beer_rating) -> sortString = BeerEntry.COLUMN_BEER_RATING
                getString(R.string.sort_beer_date) -> sortString = BeerEntry.COLUMN_BEER_DATE
                getString(R.string.sort_beer_bitterness) -> sortString = BeerEntry.COLUMN_BEER_IBU
                getString(R.string.sort_beer_location) -> {
                    sortString = BeerEntry.COLUMN_BREWERY_COUNTRY
                    sortString += if (sortDirection == ASC)
                        " ASC, "
                    else
                        " DESC, "
                    sortString += BeerEntry.COLUMN_BREWERY_NAME
                }
            }

            sortString += if (sortDirection == ASC)
                " ASC, "
            else
                " DESC, "

            sortString += BeerEntry.COLUMN_BEER_NAME + " ASC"
        }

        Log.d(LOG_TAG, sortString)

        // Return the cursorLoader that will execute the content providers query method
        return CursorLoader(activity,
                BeerEntry.CONTENT_URI,
                projection, null, null,
                sortString) // TODO Add some logic to sort the data here.
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        // Update the cursorAdapter with updated data
        cursorAdapter.swapCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        // Callback when the data needs to be deleted
        cursorAdapter.swapCursor(null)
    }

    companion object {

        private val LOG_TAG = MainActivity::class.java.simpleName

        // Device dimensions for creating thumbnails
        var THUMB_SMALL_W: Int = 0
        var THUMB_LARGE_W: Int = 0

        // Identifier for the loader
        private val BEER_LOADER = 0

        // Sort direction
        private val ASC = true
    }
}
