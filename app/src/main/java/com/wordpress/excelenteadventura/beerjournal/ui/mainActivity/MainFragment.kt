package com.wordpress.excelenteadventura.beerjournal.ui.mainActivity


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import com.wordpress.excelenteadventura.beerjournal.InjectorUtils
import com.wordpress.excelenteadventura.beerjournal.R
import com.wordpress.excelenteadventura.beerjournal.SortOrderActivity
import com.wordpress.excelenteadventura.beerjournal.database.Beer
import com.wordpress.excelenteadventura.beerjournal.ui.addBeerActivity.AddBeerActivity
import kotlinx.android.synthetic.main.fragment_main.view.*


/**
 * A simple [Fragment] subclass.
 */
class MainFragment : Fragment() {

    private val LOG_TAG = MainActivity::class.java.simpleName
    // Sort direction
    private val ASC = true

    private lateinit var mainActivityViewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val fragmentView = inflater.inflate(R.layout.fragment_main, container, false)

        // Setup the recycler view
        val beerListAdapter = BeerListAdapter(context)
        val recyclerView = fragmentView.main_fragment_recycler_view
        recyclerView.apply {
            adapter = beerListAdapter
            layoutManager = LinearLayoutManager(context)
        }

        // Get a viewmodel and set up the observers
        val factory = InjectorUtils.provideMainActivityViewModelFactory(activity)
        mainActivityViewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
        mainActivityViewModel.getAllBeers().observe(this,
                Observer<List<Beer>> { beers -> beerListAdapter.setBeers(beers!!) }
        )

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

        // This line enables the fragment to handle menu events
        setHasOptionsMenu(true)

        // Setup Floating Action Button to open next activity
        fragmentView.floating_action_button.setOnClickListener {
            // Launches AddBeerActivity
            val intent = Intent(activity, AddBeerActivity::class.java)
            startActivityForResult(intent, NEW_BEER_ACTIVITY_REQUEST_CODE)
        }

//        // Setup the list item on click listener. Jumps to editor activity.
//        recyclerView.onItemClickListener = AdapterView.OnItemClickListener { _, _, _, id ->
//            // Create new intent to go to the AddBeer Activity
//            val intent = Intent(activity, AddBeerActivity::class.java)
//
//            // Create the content URI to pass through with the intent which represents the
//            // Beer item that was clicked on.
//            val currentBeerUri = ContentUris.withAppendedId(BeerEntry.CONTENT_URI, id)
//
//            // Set the URI on the data field of the intent and launch the activity
//            intent.data = currentBeerUri
//            startActivity(intent)
//        }

        return fragmentView
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == NEW_BEER_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
//            val beer = Word(data!!.getStringExtra(NewWordActivity.EXTRA_REPLY))
//            mWordViewModel.insert(word)
//        } else {
//            Toast.makeText(
//                    getApplicationContext(),
//                    R.string.empty_not_saved,
//                    Toast.LENGTH_LONG).show()
//        }
//    }

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

    companion object {
        const val NEW_BEER_ACTIVITY_REQUEST_CODE = 1

        // Device dimensions for creating thumbnails
        var THUMB_SMALL_W: Int = 0
        var THUMB_LARGE_W: Int = 0
    }
}
