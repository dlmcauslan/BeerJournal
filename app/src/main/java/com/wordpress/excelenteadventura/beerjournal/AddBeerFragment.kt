package com.wordpress.excelenteadventura.beerjournal


import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.FileProvider
import android.support.v4.content.Loader
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.AdapterView
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

import com.wordpress.excelenteadventura.beerjournal.database.BeerContract.BeerEntry

import java.io.File
import java.io.IOException
import java.util.ArrayList
import java.util.Arrays

import android.app.Activity.RESULT_OK


/**
 * A simple [Fragment] subclass.
 */
class AddBeerFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {

    // Content URI for the existing beer (null if its a new beer)
    private var currentBeerUri: Uri? = null

    // Data edit fields
    private var mBeerNameEditText: EditText? = null
    private var mPercentageEditText: EditText? = null
    private var mBitternessEditText: EditText? = null
    private var mBreweryNameEditText: EditText? = null
    private var mCityEditText: EditText? = null
    private var mStateEditText: EditText? = null
    private var mCountryEditText: EditText? = null
    private var mCommentsEditText: EditText? = null
    private var mBeerTypeSpinner: Spinner? = null
    private var mBeerTypeEdit: EditText? = null
    private var mBeerRatingSpinner: Spinner? = null
    private var mDatePicker: DatePicker? = null
    private var mBeerImageView: ImageView? = null

    // An ArrayList to hold Strings that contain the paths to the photos.
    private var mPhotoPath = ArrayList<String>()

    // TODO: Possibly set default spinner values here.

    // Boolean flag that keeps track of whether the beer has been edited
    private var mBeerChanged: Boolean? = false

    // OnTouchListener that listens for whether user touches a view, meaning the data
    // has most likely been changed.
    // Changes mBeerChanged flag.
    private val mTouchListener = View.OnTouchListener { _, _ ->
        mBeerChanged = true
        false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val fragment = inflater.inflate(R.layout.fragment_add_beer, container, false)

        Log.v(LOG_TAG, "pixels: " + Companion.getTHUMB_SMALL_W() + " " + Companion.getTHUMB_LARGE_W())

        // Examine the intent that was used to create this activity.
        // Check whether we've launched an addNewBeer or an EditBeer
        val intent = activity.intent
        currentBeerUri = intent.data

        // This line enables the fragment to handle menu events
        setHasOptionsMenu(true)
        // If the intent does not contain a Beer content URI then we are adding a new beer
        if (currentBeerUri == null) {
            activity.title = getString(R.string.add_beer_title)
            // Invalidate the options menu, so the delete option isn't shown
            activity.invalidateOptionsMenu()
        } else {
            activity.title = getString(R.string.edit_beer_title)
            // Initialize a loader to read the beer data from database and display it in the
            // relevant fields
            loaderManager.initLoader(EXISTING_BEER_LOADER, null, this)
        }

        // Find all the relevant views that we need to read user data from
        mBeerNameEditText = fragment.findViewById(R.id.edit_beer_name) as EditText
        mBeerImageView = fragment.findViewById(R.id.image_beer_photo) as ImageView
        mBeerTypeSpinner = fragment.findViewById(R.id.spinner_beer_type) as Spinner
        mBeerTypeEdit = fragment.findViewById(R.id.edit_beer_type) as EditText
        mBeerRatingSpinner = fragment.findViewById(R.id.spinner_beer_rating) as Spinner
        mPercentageEditText = fragment.findViewById(R.id.edit_beer_percentage) as EditText
        mBitternessEditText = fragment.findViewById(R.id.edit_beer_bitterness) as EditText
        mBreweryNameEditText = fragment.findViewById(R.id.edit_brewery_name) as EditText
        mCityEditText = fragment.findViewById(R.id.edit_city) as EditText
        mStateEditText = fragment.findViewById(R.id.edit_state) as EditText
        mCountryEditText = fragment.findViewById(R.id.edit_country) as EditText
        mCommentsEditText = fragment.findViewById(R.id.edite_beer_comments) as EditText
        mDatePicker = fragment.findViewById(R.id.date_picker) as DatePicker

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
        mBeerTypeSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(arg0: AdapterView<*>, arg1: View, arg2: Int, arg3: Long) {

                val item = mBeerTypeSpinner!!.selectedItem.toString()
                Log.i("Selected item : ", item)
                if (item == getString(R.string.other)) {
                    mBeerTypeEdit!!.visibility = View.VISIBLE
                } else {
                    mBeerTypeEdit!!.visibility = View.GONE
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }

        }

        // On click listener for Add Photo text
        val takePhoto = fragment.findViewById(R.id.add_beer_take_photo) as TextView
        takePhoto.setOnClickListener { startCameraIntent() }

        // On click listener for photo to open imagesActivity
        mBeerImageView!!.setOnClickListener {
            val intent: Intent
            // If the photo is null or empty then don't pass the intent
            if (mPhotoPath.isEmpty() || mPhotoPath[0].isEmpty()) {
                startCameraIntent()
            } else if (mPhotoPath.size == 1) {
                // Opens the image in gallery
                val uri = Uri.fromFile(File(mPhotoPath[0]))
                intent = Intent(android.content.Intent.ACTION_VIEW)
                var mime: String? = "*/*"
                val mimeTypeMap = MimeTypeMap.getSingleton()

                if (mimeTypeMap.hasExtension(
                                mimeTypeMap.getFileExtensionFromUrl(uri.toString())))
                    mime = mimeTypeMap.getMimeTypeFromExtension(
                            mimeTypeMap.getFileExtensionFromUrl(uri.toString()))

                intent.setDataAndType(uri, mime)
                startActivity(intent)
            } else {
                // Create new intent to go to the AddBeer Activity
                intent = Intent(activity, ImagesActivity::class.java)
                intent.putStringArrayListExtra("photosExtra", mPhotoPath)
                intent.putExtra("beerName", mBeerNameEditText!!.text.toString().trim { it <= ' ' })
                startActivity(intent)
            }// If there is only one photo then go straight to gallery when clicking on image.
        }


        return fragment
    }

    private fun startCameraIntent() {
        // Start an intent to open the camera app
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
            // Create the file where the photo should go
            var photoFile: File? = null
            try {
                photoFile = Utilities.createImageFile(context)
                // Get the path for this photo and add it to mPhotoPath arraylist.
                if (!mPhotoPath.isEmpty() && mPhotoPath[0].isEmpty()) {
                    // If the first photo is empty then replace it.
                    mPhotoPath.removeAt(0)
                    mPhotoPath.add(0, photoFile!!.absolutePath)
                    Log.d(LOG_TAG, "Replacing empty photo.")
                } else {
                    mPhotoPath.add(photoFile!!.absolutePath)
                }
                Log.i(LOG_TAG, "path: " + photoFile.absolutePath)

            } catch (ex: IOException) {
                // Error occured whilst creating the file.
                Toast.makeText(activity, getString(R.string.photo_save_failed), Toast.LENGTH_SHORT).show()
            }

            // Continue if the file was successfully created.
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(activity, "com.example.android.fileprovider", photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    // Code that gets the result of the photograph and adds it to the imageView.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val numPhotos = mPhotoPath.size - 1
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //            for (String s : mPhotoPath) {
            //                Log.d(LOG_TAG, "path here is: " + s);
            //            }
            // Create a small and large thumbnail of the captured image, then set the large
            // thumbnail to the imageview.
            Utilities.createThumbnail(mPhotoPath[numPhotos], Companion.getTHUMB_SMALL_W())
            Utilities.createThumbnail(mPhotoPath[numPhotos], Companion.getTHUMB_LARGE_W())
            Utilities.setThumbnailFromWidth(mBeerImageView!!, mPhotoPath[0], Companion.getTHUMB_LARGE_W())
        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            // Otherwise remove the path because the photo was not saved
            //            Log.d(LOG_TAG, "resultCode not good, path is" + mPhotoPath.get(numPhotos));
            mPhotoPath.removeAt(numPhotos)

        }
    }

    // Saves a beer to the database from the input fields
    private fun saveBeer() {
        // Read from input fields. Use trim to eliminate excess white space.
        val beerName = mBeerNameEditText!!.text.toString().trim { it <= ' ' }
        var beerType = mBeerTypeSpinner!!.selectedItem.toString()
        // If beerType is other set beerType to EditText string
        if (beerType == getString(R.string.other)) {
            beerType = mBeerTypeEdit!!.text.toString().trim { it <= ' ' }
        }
        val ratingString = mBeerRatingSpinner!!.selectedItem.toString()
        val percentageString = mPercentageEditText!!.text.toString()
        val bitternessString = mBitternessEditText!!.text.toString()
        var breweryName = mBreweryNameEditText!!.text.toString().trim { it <= ' ' }
        var city = mCityEditText!!.text.toString().trim { it <= ' ' }
        val state = mStateEditText!!.text.toString().trim { it <= ' ' }
        var country = mCountryEditText!!.text.toString().trim { it <= ' ' }
        val comments = mCommentsEditText!!.text.toString().trim { it <= ' ' }
        val year = mDatePicker!!.year
        val month = mDatePicker!!.month
        val day = mDatePicker!!.dayOfMonth
        val date = year.toString() + "-" + month + "-" + day

        // Check that BeerName has an entry. Popup a toast to alert user.
        if (TextUtils.isEmpty(beerName)) {
            Toast.makeText(activity, "Beer must have a name!", Toast.LENGTH_SHORT).show()
            return
        }

        // Set default values if fields are empty
        if (TextUtils.isEmpty(breweryName)) breweryName = BeerEntry.DEAULT_STRING
        if (TextUtils.isEmpty(city)) city = BeerEntry.DEAULT_STRING
        if (TextUtils.isEmpty(country)) country = BeerEntry.DEAULT_STRING
        var bitterness = -1
        if (!TextUtils.isEmpty(bitternessString)) bitterness = Integer.parseInt(bitternessString)
        var percentage = -1.0
        if (!TextUtils.isEmpty(percentageString)) percentage = java.lang.Double.parseDouble(percentageString)
        if (beerType == "---") beerType = BeerEntry.DEAULT_STRING
        var rating = 0
        if (ratingString != "---") rating = (2 * java.lang.Double.parseDouble(ratingString)).toInt()

        // Create a ContentValues object where the column names are the keys, and the values
        // are from the form fields.
        val values = ContentValues()
        values.put(BeerEntry.COLUMN_BEER_NAME, beerName)
        values.put(BeerEntry.COLUMN_BEER_TYPE, beerType)
        values.put(BeerEntry.COLUMN_BEER_RATING, rating)
        values.put(BeerEntry.COLUMN_BEER_PERCENTAGE, percentage)
        values.put(BeerEntry.COLUMN_BEER_IBU, bitterness)
        values.put(BeerEntry.COLUMN_BREWERY_NAME, breweryName)
        values.put(BeerEntry.COLUMN_BREWERY_CITY, city)
        values.put(BeerEntry.COLUMN_BREWERY_STATE, state)
        values.put(BeerEntry.COLUMN_BREWERY_COUNTRY, country)
        values.put(BeerEntry.COLUMN_BEER_COMMENTS, comments)
        values.put(BeerEntry.COLUMN_BEER_DATE, date)
        values.put(BeerEntry.COLUMN_BEER_PHOTO, Utilities.listToString(mPhotoPath))

        Log.d(LOG_TAG, "name: " + beerName + ", rating: " + rating + ", percentage: " + percentage + ", bitterness: "
                + bitterness + ", breweryName: " + breweryName + ", date: " + date)
        Log.d(LOG_TAG, "IMAGES: " + Utilities.listToString(mPhotoPath))

        // Determine if this is a new or existing Beer
        if (currentBeerUri == null) {
            // Add a new beer
            val newUri = activity.contentResolver.insert(BeerEntry.CONTENT_URI, values)
            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null)
                Toast.makeText(activity, "Insert new beer failed.", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(activity, "New beer successfully added to database.", Toast.LENGTH_SHORT).show()
        } else {
            // This is an existing beer entry, so update the database with the new values
            val rowsAffected = activity.contentResolver.update(currentBeerUri!!, values, null, null)
            // Show a toast to let the user know whether the beer was updated successfully
            if (rowsAffected == 0)
                Toast.makeText(activity, "Update beer data failed.", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(activity, "Beer data updated successfully.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Deletes the current beer from the database
     */
    private fun deleteBeer() {
        // Only perform the deletion if it is an existing beer
        if (currentBeerUri != null) {
            // TODO delete images associated with the beer item.
            for (fileName in mPhotoPath) {
                // Delete image
                val imageFile = File(fileName)
                val deleteSuccessful = imageFile.delete()
                if (deleteSuccessful)
                    Log.v(LOG_TAG, "Delete successful: $fileName")
                else
                    Log.v(LOG_TAG, "Delete failed: $fileName")
                // Delete small thumbnail
                val thumbFileName = Utilities.thumbFilePath(fileName, Companion.getTHUMB_SMALL_W())
                val thumbFile = File(thumbFileName)
                val thumbDeleteSuccessful = thumbFile.delete()
                if (thumbDeleteSuccessful)
                    Log.v(LOG_TAG, "Thumbnail delete successful: $thumbFileName")
                else
                    Log.v(LOG_TAG, "Thumbnail delete failed: $thumbFileName")
                // Delete large thumbnail
                val thumbLargeFileName = Utilities.thumbFilePath(fileName, Companion.getTHUMB_LARGE_W())
                val thumbLargeFile = File(thumbLargeFileName)
                val thumbLargeDeleteSuccessful = thumbFile.delete()
                if (thumbLargeDeleteSuccessful)
                    Log.v(LOG_TAG, "Thumbnail delete successful: $thumbLargeFile")
                else
                    Log.v(LOG_TAG, "Thumbnail delete failed: $thumbLargeFileName")
            }
            // Call the content resolver to delete the beer from database
            val rowsDeleted = activity.contentResolver.delete(currentBeerUri!!, null, null)
            // Show a toast message depending on whether or not the delete was successfull
            if (rowsDeleted == 0)
                Toast.makeText(activity, getString(R.string.editor_delete_beer_failed), Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(activity, getString(R.string.editor_delete_beer_successful), Toast.LENGTH_SHORT).show()
        }
        // Close the activity
        activity.finish()
    }

    // Options menu code
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        activity.menuInflater.inflate(R.menu.menu_editor, menu)
    }

    // This allows some menu items to be hidden or made visible.
    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        // If it is a new beer, hide the Delete menu item.
        if (currentBeerUri == null) {
            val menuItem = menu!!.findItem(R.id.action_delete)
            menuItem.isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // Perform different activities based on which item the user clicks
        when (item!!.itemId) {
        // Click on save menu item
            R.id.action_save -> {
                // Save beer, then exit activity
                saveBeer()
                activity.finish()
                return true
            }
        // Click on delete menu option
            R.id.action_delete -> {
                // Delete beer
                showDeleteConfirmationDialog()
                return true
            }
        // Click on android up button in app bar
            android.R.id.home -> {
                // TODO
                activity.finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // TODO implement onBAckPressed method

    override fun onCreateLoader(id: Int, args: Bundle): Loader<Cursor> {
        // Projection specifying the columns we wish to load
        val projection = arrayOf(BeerEntry._ID, BeerEntry.COLUMN_BEER_NAME, BeerEntry.COLUMN_BEER_TYPE, BeerEntry.COLUMN_BEER_IBU, BeerEntry.COLUMN_BREWERY_NAME, BeerEntry.COLUMN_BREWERY_CITY, BeerEntry.COLUMN_BREWERY_STATE, BeerEntry.COLUMN_BREWERY_COUNTRY, BeerEntry.COLUMN_BEER_DATE, BeerEntry.COLUMN_BEER_PERCENTAGE, BeerEntry.COLUMN_BEER_RATING, BeerEntry.COLUMN_BEER_COMMENTS, BeerEntry.COLUMN_BEER_PHOTO)

        // Return the cursorLoader that will execute the content providers query method
        return CursorLoader(activity,
                currentBeerUri,
                projection, null, null, null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor?) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.count < 1) return

        // Move to the first row of the cursor (should be only row) and read data from it
        if (cursor.moveToFirst()) {
            // Get the data columns we're interested in
            val beerNameColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_NAME)
            val beerTypeColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_TYPE)
            val beerIBUColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_IBU)
            val breweryNameColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BREWERY_NAME)
            val cityColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BREWERY_CITY)
            val stateColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BREWERY_STATE)
            val countryColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BREWERY_COUNTRY)
            val dateColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_DATE)
            val percentageColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_PERCENTAGE)
            val ratingColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_RATING)
            val commentsColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_COMMENTS)
            val imageColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_PHOTO)

            // Read the beer attributes from the cursor for the current beer
            val beerName = cursor.getString(beerNameColumn)
            val beerType = cursor.getString(beerTypeColumn)
            val bitterness = cursor.getInt(beerIBUColumn)
            var breweryName = cursor.getString(breweryNameColumn)
            var city = cursor.getString(cityColumn)
            val state = cursor.getString(stateColumn)
            var country = cursor.getString(countryColumn)
            val date = cursor.getString(dateColumn)
            val comments = cursor.getString(commentsColumn)
            val percentage = cursor.getDouble(percentageColumn)
            val rating = cursor.getInt(ratingColumn).toDouble() / 2
            val imageStrings = cursor.getString(imageColumn)
            mPhotoPath = Utilities.stringToList(imageStrings)

            // TODO Handle default values

            // Update the edit text views with the attributes for the current beer
            mBeerNameEditText!!.setText(beerName)
            if (breweryName == BeerEntry.DEAULT_STRING) breweryName = ""
            mBreweryNameEditText!!.setText(breweryName)
            if (city == BeerEntry.DEAULT_STRING) city = ""
            mCityEditText!!.setText(city)
            mStateEditText!!.setText(state)
            if (country == BeerEntry.DEAULT_STRING) country = ""
            mCountryEditText!!.setText(country)
            mCommentsEditText!!.setText(comments)
            if (percentage < 0)
                mPercentageEditText!!.setText("")
            else
                mPercentageEditText!!.setText(percentage.toString())
            if (bitterness < 0)
                mBitternessEditText!!.setText("")
            else
                mBitternessEditText!!.setText(bitterness.toString())

            // Update the spinners
            // If beertype equals "unknown" set the spinner to ---
            if (beerType == BeerEntry.DEAULT_STRING) {
                mBeerTypeSpinner!!.setSelection(0)
            } else {
                val typeArray = Arrays.asList(*resources.getStringArray(R.array.array_beer_type_options))
                val itemIndex = typeArray.indexOf(beerType)
                // If itemIndex >=0 set the selection to that item. Otherwise the string is not in the spinner
                // So set the spinner to other, show the edit text field and set the text.
                if (itemIndex >= 0) {
                    mBeerTypeSpinner!!.setSelection(itemIndex)
                } else {
                    mBeerTypeSpinner!!.setSelection(typeArray.indexOf(getString(R.string.other)))
                    mBeerTypeEdit!!.visibility = View.VISIBLE
                    mBeerTypeEdit!!.setText(beerType)
                }
                //                Log.d(LOG_TAG, "type " + beerType + " " + typeArray.indexOf(beerType));

            }// If beertype equals something that is not
            val ratingArray = Arrays.asList(*resources.getStringArray(R.array.array_rating_options))
            mBeerRatingSpinner!!.setSelection(ratingArray.indexOf(rating.toString()))
            //            Log.d(LOG_TAG, "rating " + String.valueOf(rating));

            // Update the date picker
            val dateArray = date.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            mDatePicker!!.updateDate(Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1]), Integer.parseInt(dateArray[2]))

            // Update the image view
            Utilities.setThumbnailFromWidth(mBeerImageView!!, mPhotoPath[0], Companion.getTHUMB_LARGE_W())
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mBeerNameEditText!!.setText("")
        mBreweryNameEditText!!.setText("")
        mCityEditText!!.setText("")
        mStateEditText!!.setText("")
        mCountryEditText!!.setText("")
        mCommentsEditText!!.setText("")
        mPercentageEditText!!.setText("")
        mBitternessEditText!!.setText("")
        mBeerRatingSpinner!!.setSelection(0)
        mBeerTypeSpinner!!.setSelection(0)
        // TODO set this to some default image.
        mBeerImageView!!.setImageBitmap(null)
    }

    /**
     * Prompts the user to make sure they want to delete the beer.
     */
    private fun showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message. And click listeners
        // for the positive and negative buttons on the dialog.
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(R.string.delete_dialog_msg)
        builder.setPositiveButton(R.string.delete) { _, _ ->
            // User clicked the delete button so delete Beer
            deleteBeer()
        }
        builder.setNegativeButton(R.string.cancel) { dialog, _ ->
            // User clicked the cancel button, so dismiss dialog and continue editing
            dialog?.dismiss()
        }

        // Create and show the Alert Dialog
        val alertDialog = builder.create()
        alertDialog.show()
    }

    companion object {

        private val LOG_TAG = AddBeerActivity::class.java.simpleName

        private const val EXISTING_BEER_LOADER = 0
        internal const val REQUEST_IMAGE_CAPTURE = 1
    }
}
