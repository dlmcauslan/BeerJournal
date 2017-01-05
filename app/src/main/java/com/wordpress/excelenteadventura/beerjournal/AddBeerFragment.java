package com.wordpress.excelenteadventura.beerjournal;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.wordpress.excelenteadventura.beerjournal.database.BeerContract.BeerEntry;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddBeerFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = AddBeerActivity.class.getSimpleName();

    private static final int EXISTING_BEER_LOADER = 0;
    static final int REQUEST_IMAGE_CAPTURE = 1;

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

    // An ArrayList to hold Strings that contain the paths to the photos.
    private ArrayList<String> mPhotoPath = new ArrayList<String>();

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

        // On click listener for beer type spinner to show beer type edit text if other is selected
        mBeerTypeSpinner.setOnClickListener(new Spinner.OnClickListener() {
            @Override
            public void onClick(View v) {
                String beerType = mBeerTypeSpinner.getSelectedItem().toString();
                if (beerType.equals(getString(R.string.other))) {
                    
                } else {

                }
            }
        });

        // On click listener for Add Photo text
        TextView takePhoto = (TextView) mFragment.findViewById(R.id.add_beer_take_photo);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start an intent to open the camera app
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Create the file where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = Utilities.createImageFile(getContext());
                        // Get the path for this photo and add it to mPhotoPath arraylist.
                        mPhotoPath.add(photoFile.getAbsolutePath());
                        Log.i(LOG_TAG, photoFile.getAbsolutePath());

                    } catch (IOException ex) {
                        // Error occured whilst creating the file.
                        Toast.makeText(getActivity(),getString(R.string.photo_save_failed), Toast.LENGTH_SHORT).show();
                    }
                    // Continue if the file was successfully created.
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(getActivity(), "com.example.android.fileprovider", photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        });

        // TODO: Maybe need to setup spinners here. Will see if they are OK as is.
        // Will probably need to at least set up Beer Type spinner so can pop up a dialog
        // for other beer type.

        return mFragment;
    }

    // Code that gets the result of the photograph and adds it to the imageView.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            Bitmap imageBitmap = Utilities.loadImage(getContext(), mPhotoPath);
//            mBeerImageView.setImageBitmap(imageBitmap);
            // Load the first image in mPhotoPath to mBeerImageView. Note Utilities.setImage scales the image
            // to reduce memory usage.
            int desiredThumbnailWidth = 144;

            Utilities.setImage(mBeerImageView, mPhotoPath.get(0));
            Utilities.createThumbnail(mPhotoPath.get(mPhotoPath.size()-1), desiredThumbnailWidth);
            // TODO save a 144W thumbnail of the image to load, so it isn't resized every time its
            // loaded on the mainFragment.
            // TODO Maybe this should be an Async task.
//            String[] splitName = mPhotoPath.get(0).split("/");
//            String fName = splitName[splitName.length-1].split("\\.")[0] + "_thumb144";
//            Log.d(LOG_TAG, "splitname = " + fName);
        }
    }

    // Saves a beer to the database from the input fields
    private void saveBeer() {
        // Read from input fields. Use trim to eliminate excess white space.
        String beerName = mBeerNameEditText.getText().toString().trim();
        String beerType = mBeerTypeSpinner.getSelectedItem().toString();
        String ratingString = mBeerRatingSpinner.getSelectedItem().toString();
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
        values.put(BeerEntry.COLUMN_BEER_PHOTO, Utilities.listToString(mPhotoPath));

        Log.d(LOG_TAG, "name: " + beerName + ", rating: " + rating + ", percentage: " + percentage + ", bitterness: "
        + bitterness + ", breweryName: " + breweryName + ", date: " + date);
        Log.d(LOG_TAG, "IMAGES: " + Utilities.listToString(mPhotoPath));

        // Determine if this is a new or existing Beer
        if (mCurrentBeerUri == null) {
            // Add a new beer
            Uri newUri = getActivity().getContentResolver().insert(BeerEntry.CONTENT_URI, values);
            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) Toast.makeText(getActivity(), "Insert new beer failed.", Toast.LENGTH_SHORT).show();
            else Toast.makeText(getActivity(), "New beer successfully added to database.", Toast.LENGTH_SHORT).show();
        } else {
            // This is an existing beer entry, so update the database with the new values
            int rowsAffected = getActivity().getContentResolver().update(mCurrentBeerUri, values, null, null);
            // Show a toast to let the user know whether the beer was updated successfully
            if (rowsAffected == 0) Toast.makeText(getActivity(), "Update beer data failed.", Toast.LENGTH_SHORT).show();
            else Toast.makeText(getActivity(), "Beer data updated successfully.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Deletes the current beer from the database
     */
    private void deleteBeer() {
        // Only perform the deletion if it is an existing beer
        if (mCurrentBeerUri != null) {
            // TODO delete images associated with the beer item.

            // Call the content resolver to delete the beer from database
            int rowsDeleted = getActivity().getContentResolver().delete(mCurrentBeerUri, null, null);
            // Show a toast message depending on whether or not the delete was successfull
            if (rowsDeleted == 0) Toast.makeText(getActivity(), getString(R.string.editor_delete_beer_failed), Toast.LENGTH_SHORT).show();
            else Toast.makeText(getActivity(), getString(R.string.editor_delete_beer_successful), Toast.LENGTH_SHORT).show();
        }
        // Close the activity
        getActivity().finish();
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
                // Delete beer
                showDeleteConfirmationDialog();
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
        // Projection specifying the columns we wish to load
        String[] projection = {BeerEntry._ID,
                BeerEntry.COLUMN_BEER_NAME,
                BeerEntry.COLUMN_BEER_TYPE,
                BeerEntry.COLUMN_BEER_IBU,
                BeerEntry.COLUMN_BREWERY_NAME,
                BeerEntry.COLUMN_BREWERY_CITY,
                BeerEntry.COLUMN_BREWERY_STATE,
                BeerEntry.COLUMN_BREWERY_COUNTRY,
                BeerEntry.COLUMN_BEER_DATE,
                BeerEntry.COLUMN_BEER_PERCENTAGE,
                BeerEntry.COLUMN_BEER_RATING,
                BeerEntry.COLUMN_BEER_COMMENTS,
                BeerEntry.COLUMN_BEER_PHOTO};

        // Return the cursorLoader that will execute the content providers query method
        return new CursorLoader(getActivity(),
                mCurrentBeerUri,
                projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) return;

        // Move to the first row of the cursor (should be only row) and read data from it
        if (cursor.moveToFirst()) {
            // Get the data columns we're interested in
            int beerNameColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_NAME);
            int beerTypeColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_TYPE);
            int beerIBUColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_IBU);
            int breweryNameColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BREWERY_NAME);
            int cityColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BREWERY_CITY);
            int stateColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BREWERY_STATE);
            int countryColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BREWERY_COUNTRY);
            int dateColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_DATE);
            int percentageColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_PERCENTAGE);
            int ratingColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_RATING);
            int commentsColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_COMMENTS);
            int imageColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_PHOTO);

            // Read the beer attributes from the cursor for the current beer
            String beerName = cursor.getString(beerNameColumn);
            String beerType = cursor.getString(beerTypeColumn);
            int bitterness = cursor.getInt(beerIBUColumn);
            String breweryName = cursor.getString(breweryNameColumn);
            String city = cursor.getString(cityColumn);
            String state = cursor.getString(stateColumn);
            String country = cursor.getString(countryColumn);
            String date = cursor.getString(dateColumn);
            String comments = cursor.getString(commentsColumn);
            double percentage = cursor.getDouble(percentageColumn);
            double rating = ((double)cursor.getInt(ratingColumn)/2);
            String imageStrings = cursor.getString(imageColumn);
            mPhotoPath = Utilities.stringToList(imageStrings);

            // TODO Handle default values

            // Update the edit text views with the attributes for the current beer
            mBeerNameEditText.setText(beerName);
            mBreweryNameEditText.setText(breweryName);
            mCityEditText.setText(city);
            mStateEditText.setText(state);
            mCountryEditText.setText(country);
            mCommentsEditText.setText(comments);
            mPercentageEditText.setText(String.valueOf(percentage));
            mBitternessEditText.setText(String.valueOf(bitterness));

            // Update the spinners
            if (beerType.equals(BeerEntry.DEAULT_STRING)) mBeerTypeSpinner.setSelection(0);
            else {
                List<String> typeArray = Arrays.asList(getResources().getStringArray(R.array.array_beer_type_options));
                mBeerTypeSpinner.setSelection(typeArray.indexOf(beerType));
//                Log.d(LOG_TAG, "type " + beerType + " " + typeArray.indexOf(beerType));

            }
            List<String> ratingArray = Arrays.asList(getResources().getStringArray(R.array.array_rating_options));
            mBeerRatingSpinner.setSelection(ratingArray.indexOf(String.valueOf(rating)));
//            Log.d(LOG_TAG, "rating " + String.valueOf(rating));

            // Update the date picker
            String[] dateArray = date.split("-");
            mDatePicker.updateDate(Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1]), Integer.parseInt(dateArray[2]));

            // Update the image view
            Utilities.setImage(mBeerImageView, mPhotoPath.get(0));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mBeerNameEditText.setText("");
        mBreweryNameEditText.setText("");
        mCityEditText.setText("");
        mStateEditText.setText("");
        mCountryEditText.setText("");
        mCommentsEditText.setText("");
        mPercentageEditText.setText("");
        mBitternessEditText.setText("");
        mBeerRatingSpinner.setSelection(0);
        mBeerTypeSpinner.setSelection(0);
        // TODO set this to some default image.
        mBeerImageView.setImageBitmap(null);
    }

    /**
     * Prompts the user to make sure they want to delete the beer.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message. And click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the delete button so delete Beer
                deleteBeer();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked the cancel button, so dismiss dialog and continue editing
                if (dialog != null) dialog.dismiss();
            }
        });

        // Create and show the Alert Dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
