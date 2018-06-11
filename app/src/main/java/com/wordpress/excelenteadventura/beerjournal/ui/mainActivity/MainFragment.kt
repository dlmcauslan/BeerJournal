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
import com.wordpress.excelenteadventura.beerjournal.BeerRepository
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

    private lateinit var viewModel: MainViewModel
    private lateinit var repository: BeerRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val fragmentView = inflater.inflate(R.layout.fragment_main, container, false)

        // Setup the recycler view
        val beerListAdapter = BeerListAdapter(context, beerItemClickListener)
        val recyclerView = fragmentView.main_fragment_recycler_view
        recyclerView.apply {
            adapter = beerListAdapter
            layoutManager = LinearLayoutManager(context)
        }

        // Get a viewmodel and set up the observers
        val factory = InjectorUtils.provideMainActivityViewModelFactory(activity)
        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
        viewModel.beers.observe(this,
                Observer<List<Beer>> { beers -> beerListAdapter.setBeers(beers!!) }
        )

        // Setup the repository
        repository = InjectorUtils.provideRepository(activity)

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
            // Set current beer to null
            repository.currentBeer.value = null
            // Launches AddBeerActivity
            startActivity(Intent(activity, AddBeerActivity::class.java))
        }

        return fragmentView
    }

    // On Click listener for the recycler view.
    private val beerItemClickListener = object : BeerListAdapter.OnItemClickListener {
        override fun onItemClick(item: Beer) {
            // Set the current beer
            repository.currentBeer.value = item

            // Create new intent to go to the AddBeer Activity
            startActivity(Intent(activity, AddBeerActivity::class.java))
        }
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
}

// Device dimensions for creating thumbnails
var THUMB_SMALL_W: Int = 0
var THUMB_LARGE_W: Int = 0
