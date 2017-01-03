package com.wordpress.excelenteadventura.beerjournal;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.wordpress.excelenteadventura.beerjournal.database.BeerContract.BeerEntry;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddBeerFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = AddBeerActivity.class.getSimpleName();

    private static final int EXISTING_BEER_LOADER = 0;

    // Content URI for the existing beer (null if its a new beer)
    private Uri mCurrentBeerUri;

    // Data edit fields
    private EditText mBeerNameEditText;
    private EditText mPercentageEditText;
    private EditText mBitternessEditText;
    private EditText mBreweryNameEditText;
    private EditText mCityEditText;
    private EditText mStateEditText;
    private EditText mCountryEditText;
    private EditText mCommentsEditText;
    private Spinner mBeerTypeSpinner;
    private Spinner mBeerRatingSpinner;
    private DatePicker mDatePicker;
    private ImageView mBeerImageView;

    // TODO: Possibly set default spinner values here.

    // Boolean flag that keeps track of whether the beer has been edited
    private Boolean mBeerChanged = false;

    // OnTouchListener that listens for whether user touches a view, meaning the data
    // has most likely been changed.
    // Changes mBeerChanged flag.
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mBeerChanged = true;
            return false;
        }
    };


    public AddBeerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mFragment = inflater.inflate(R.layout.fragment_add_beer, container, false);

        // Examine the intent that was used to create this activity.
        // Check whether we've launched an addNewBeer or an EditBeer
        Intent intent = getActivity().getIntent();
        mCurrentBeerUri = intent.getData();


        // This line enables the fragment to handle menu events
        setHasOptionsMenu(true);
        // If the intent does not contain a Beer content URI then we are adding a new beer
        if (mCurrentBeerUri == null) {
            getActivity().setTitle(getString(R.string.add_beer_title));
            // Invalidate the options menu, so the delete option isn't shown
            getActivity().invalidateOptionsMenu();
        } else {
            getActivity().setTitle(getString(R.string.edit_beer_title));
            // Initialize a loader to read the beer data from database and display it in the
            // relevant fields
            getLoaderManager().initLoader(EXISTING_BEER_LOADER, null, this);
        }

        // Find all the relevant views that we need to read user data from
        mBeerNameEditText = (EditText) mFragment.findViewById(R.id.edit_beer_name);
        mBeerImageView = (ImageView) mFragment.findViewById(R.id.image_beer_photo);
        mBeerTypeSpinner = (Spinner) mFragment.findViewById(R.id.spinner_beer_type);
        mBeerRatingSpinner = (Spinner) mFragment.findViewById(R.id.spinner_beer_rating);
        mPercentageEditText = (EditText) mFragment.findViewById(R.id.edit_beer_percentage);
        mBitternessEditText = (EditText) mFragment.findViewById(R.id.edit_beer_bitterness);
        mBreweryNameEditText = (EditText) mFragment.findViewById(R.id.edit_brewery_name);
        mCityEditText = (EditText) mFragment.findViewById(R.id.edit_city);
        mStateEditText = (EditText) mFragment.findViewById(R.id.edit_state);
        mCountryEditText = (EditText) mFragment.findViewById(R.id.edit_country);
        mCommentsEditText = (EditText) mFragment.findViewById(R.id.edite_beer_comments);
        mDatePicker = (DatePicker) mFragment.findViewById(R.id.date_picker);

        // Setup OnTouchListeners on all of the inputfields so we can determine if they have
        // been modified. This can be used to let the user know if they try to leave the editor
        // without saving changes.
        // TODO make this work
//        mBeerNameEditText.setOnTouchListener(mTouchListener);
//        mBeerImageView.setOnTouchListener(mTouchListener);
//        mBeerTypeSpinner.setOnTouchListener(mTouchListener);
//        mBeerRatingSpinner.setOnTouchListener(mTouchListener);
//        mPercentageEditText.setOnTouchListener(mTouchListener);
//        mBitternessEditText.setOnTouchListener(mTouchListener);
//        mBreweryNameEditText.setOnTouchListener(mTouchListener);
//        mCityEditText.setOnTouchListener(mTouchListener);
//        mStateEditText.setOnTouchListener(mTouchListener);
//        mCountryEditText.setOnTouchListener(mTouchListener);
//        mCommentsEditText.setOnTouchListener(mTouchListener);
//        mDatePicker.setOnTouchListener(mTouchListener);

        // TODO: Maybe need to setup spinners here. Will see if they are OK as is.
        // Will probably need to at least set up Beer Type spinner so can pop up a dialog
        // for other beer type.

        return mFragment;
    }

    // Saves a beer to the database from the input fields
    private void saveBeer() {
        // Read from input fields. Use trim to eliminate excess white space.
        String beerName = mBeerNameEditText.getText().toString().trim();
        String beerType = mBeerTypeSpinner.getSelectedItem().toString();
        String ratingString = mBeerRatingSpinner.getSelectedItem().toString();
//        int rating = (int) (2*((Float) mBeerRatingSpinner.getSelectedItem())); // This will likely not work
//        double percentage = Double.parseDouble(mPercentageEditText.getText().toString());
//        int bitterness = Integer.parseInt();
        String percentageString = mPercentageEditText.getText().toString();
        String bitternessString = mBitternessEditText.getText().toString();
        String breweryName = mBreweryNameEditText.getText().toString().trim();
        String city = mCityEditText.getText().toString().trim();
        String state = mStateEditText.getText().toString().trim();
        String country = mCountryEditText.getText().toString().trim();
        String comments = mCommentsEditText.getText().toString().trim();
        int year = mDatePicker.getYear();
        int month = mDatePicker.getMonth();
        int day = mDatePicker.getDayOfMonth();
        String date = year + "-" + month + "-" + day;
        // TODO: get image data

        // Check that BeerName has an entry. Popup a toast to alert user.
        if (TextUtils.isEmpty(beerName)) {
            Toast.makeText(getActivity(), "Beer must have a name!", Toast.LENGTH_SHORT).show();
            return;
        }


        // Set default values if fields are empty
        if (TextUtils.isEmpty(breweryName)) breweryName = BeerEntry.DEAULT_STRING;
        if (TextUtils.isEmpty(city)) city = BeerEntry.DEAULT_STRING;
        if (TextUtils.isEmpty(country)) country = BeerEntry.DEAULT_STRING;
        int bitterness = -1;
        if (!TextUtils.isEmpty(bitternessString)) bitterness = Integer.parseInt(bitternessString);
        double percentage = -1;
        if (!TextUtils.isEmpty(percentageString)) percentage = Double.parseDouble(percentageString);
        if (beerType.equals("---")) beerType = BeerEntry.DEAULT_STRING;
        int rating = 0;
        if (!ratingString.equals("---")) rating = (int) (2 * Double.parseDouble(ratingString));


        // Create a ContentValues object where the column names are the keys, and the values
        // are from the form fields.
        ContentValues values = new ContentValues();
        values.put(BeerEntry.COLUMN_BEER_NAME, beerName);
        values.put(BeerEntry.COLUMN_BEER_TYPE, beerType);
        values.put(BeerEntry.COLUMN_BEER_RATING, rating);
        values.put(BeerEntry.COLUMN_BEER_PERCENTAGE, percentage);
        values.put(BeerEntry.COLUMN_BEER_IBU, bitterness);
        values.put(BeerEntry.COLUMN_BREWERY_NAME, breweryName);
        values.put(BeerEntry.COLUMN_BREWERY_CITY, city);
        values.put(BeerEntry.COLUMN_BREWERY_STATE, state);
        values.put(BeerEntry.COLUMN_BREWERY_COUNTRY, country);
        values.put(BeerEntry.COLUMN_BEER_COMMENTS, comments);
        values.put(BeerEntry.COLUMN_BEER_DATE, date);

        Log.d(LOG_TAG, "name: " + beerName + ", rating: " + rating + ", percentage: " + percentage + ", bitterness: "
        + bitterness + ", breweryName: " + breweryName + ", date: " + date);

        // Determine if this is a new or existing Beer
        if (mCurrentBeerUri == null) {
            // Add a new beer
            Uri newUri = getActivity().getContentResolver().insert(BeerEntry.CONTENT_URI, values);
            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) Toast.makeText(getActivity(), "Insert new beer failed.", Toast.LENGTH_SHORT).show();
            else Toast.makeText(getActivity(), "New beer successfully added to database.", Toast.LENGTH_SHORT).show();
        } else {
            // TODO code for edit Beer
        }
    }

    // Options menu code
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_editor, menu);
    }

    // This allows some menu items to be hidden or made visible.
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If it is a new beer, hide the Delete menu item.
        if (mCurrentBeerUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Perform different activities based on which item the user clicks
        switch(item.getItemId()) {
            // Click on save menu item
            case R.id.action_save:
                // Save beer, then exit activity
                saveBeer();
                getActivity().finish();
                return true;
            // Click on delete menu option
            case R.id.action_delete:
                // TODO
                return true;
            // Click on android up button in app bar
            case android.R.id.home:
                // TODO
                getActivity().finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // TODO implement onBAckPressed method


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
