package com.wordpress.excelenteadventura.beerjournal.ui.addBeerActivity


import android.app.Activity.RESULT_OK
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.*
import android.webkit.MimeTypeMap
import android.widget.*
import com.wordpress.excelenteadventura.beerjournal.ImagesActivity
import com.wordpress.excelenteadventura.beerjournal.InjectorUtils
import com.wordpress.excelenteadventura.beerjournal.R
import com.wordpress.excelenteadventura.beerjournal.Utilities
import com.wordpress.excelenteadventura.beerjournal.database.Beer
import com.wordpress.excelenteadventura.beerjournal.ui.mainActivity.MainFragment.Companion.BEER_ID
import kotlinx.android.synthetic.main.fragment_add_beer.view.*
import java.io.File
import java.io.IOException
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class AddBeerFragment : Fragment() {

    // View Model
    private lateinit var viewModel: AddBeerViewModel

    // Content URI for the existing beer (null if its a new beer)
    private var beerId: Long = -1L

    // Data edit fields
    private lateinit var nameEdit: EditText
    private lateinit var percentageEdit: EditText
    private lateinit var bitternessEdit: EditText
    private lateinit var breweryNameEdit: EditText
    private lateinit var cityEdit: EditText
    private lateinit var stateEdit: EditText
    private lateinit var countryEdit: EditText
    private lateinit var commentsEdit: EditText
    private lateinit var typeSpinner: Spinner
    private lateinit var typeEdit: EditText
    private lateinit var ratingSpinner: Spinner
    private lateinit var datePicker: DatePicker
    private lateinit var beerImageView: ImageView

    // An ArrayList to hold Strings that contain the paths to the photos.
    private var photoPath = ArrayList<String>()

    // TODO: Possibly set default spinner values here.

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val fragment = inflater.inflate(R.layout.fragment_add_beer, container, false)
        nameEdit = fragment.edit_beer_name
        percentageEdit = fragment.edit_beer_percentage
        bitternessEdit = fragment.edit_beer_bitterness
        breweryNameEdit = fragment.edit_brewery_name
        cityEdit = fragment.edit_city
        stateEdit = fragment.edit_state
        countryEdit = fragment.edit_country
        commentsEdit = fragment.edit_beer_comments
        typeSpinner = fragment.spinner_beer_type
        typeEdit = fragment.edit_beer_type
        ratingSpinner = fragment.spinner_beer_rating
        datePicker = fragment.date_picker
        beerImageView = fragment.image_beer_photo

        // Setup View Model
        val factory = InjectorUtils.provideAddBeerViewModelFactory(activity)
        viewModel = ViewModelProviders.of(this, factory).get(AddBeerViewModel::class.java)

//        Log.v(LOG_TAG, "pixels: " + Companion.getTHUMB_SMALL_W() + " " + Companion.getTHUMB_LARGE_W())

        // Examine the intent that was used to create this activity.
        // Check whether we've launched an addNewBeer or an EditBeer
        val intent = activity.intent
        beerId = intent.getLongExtra(BEER_ID, -1)

        // This line enables the fragment to handle menu events
        setHasOptionsMenu(true)
        // If the intent does not contain a Beer content URI then we are adding a new beer
        if (beerId == -1L) {
            activity.title = getString(R.string.add_beer_title)
            // Invalidate the options menu, so the delete option isn't shown
            activity.invalidateOptionsMenu()
        } else activity.title = getString(R.string.edit_beer_title)

        // On click listener for beer type spinner to show beer type edit text if other is selected
        typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(arg0: AdapterView<*>, arg1: View, arg2: Int, arg3: Long) {

                val item = typeSpinner.selectedItem.toString()
                Log.i("Selected item : ", item)
                typeEdit.visibility = (if (item == getString(R.string.other)) View.VISIBLE else View.GONE)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }

        }

        // On click listener for Add Photo text
        val takePhoto = fragment.add_beer_take_photo
        takePhoto.setOnClickListener { startCameraIntent() }

        // On click listener for photo to open imagesActivity
        beerImageView.setOnClickListener{ beerImageClickListener }

        return fragment
    }

    private val beerImageClickListener = { _: View ->

        val intent: Intent
        // If the photo is null or empty then don't pass the intent
        if (photoPath.isEmpty() || photoPath[0].isEmpty()) {
            startCameraIntent()
        } else if (photoPath.size == 1) {
            // Opens the image in gallery
            val uri = Uri.fromFile(File(photoPath[0]))
            intent = Intent(android.content.Intent.ACTION_VIEW)
            var mime: String? = "*/*"
            val mimeTypeMap = MimeTypeMap.getSingleton()

//                if (mimeTypeMap.hasExtension(
//                                mimeTypeMap.getFileExtensionFromUrl(uri.toString())))
//                    mime = mimeTypeMap.getMimeTypeFromExtension(
//                            mimeTypeMap.getFileExtensionFromUrl(uri.toString()))

            intent.setDataAndType(uri, mime)
            startActivity(intent)
        } else {
            // Create new intent to go to the AddBeer Activity
            intent = Intent(activity, ImagesActivity::class.java)
            intent.putStringArrayListExtra("photosExtra", photoPath)
            intent.putExtra("nameEdit", nameEdit.text.toString().trim { it <= ' ' })
            startActivity(intent)
        }// If there is only one photo then go straight to gallery when clicking on image.
    }

    private fun startCameraIntent() {
        // Start an intent to open the camera app
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
            // Create the file where the photo should go
            var photoFile: File? = null
            try {
                photoFile = Utilities.createImageFile(context)
                // Get the path for this photo and add it to photoPath arraylist.
                if (!photoPath.isEmpty() && photoPath[0].isEmpty()) {
                    // If the first photo is empty then replace it.
                    photoPath.removeAt(0)
                    photoPath.add(0, photoFile.absolutePath)
                    Log.d(LOG_TAG, "Replacing empty photo.")
                } else {
                    photoPath.add(photoFile.absolutePath)
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
        val numPhotos = photoPath.size - 1
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //            for (String s : photoPath) {
            //                Log.d(LOG_TAG, "path here is: " + s);
            //            }
            // Create a small and large thumbnail of the captured image, then set the large
            // thumbnail to the imageview.
//            Utilities.createThumbnail(photoPath[numPhotos], Companion.getTHUMB_SMALL_W())
//            Utilities.createThumbnail(photoPath[numPhotos], Companion.getTHUMB_LARGE_W())
//            Utilities.setThumbnailFromWidth(beerImageView!!, photoPath[0], Companion.getTHUMB_LARGE_W())
        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            // Otherwise remove the path because the photo was not saved
            //            Log.d(LOG_TAG, "resultCode not good, path is" + photoPath.get(numPhotos));
            photoPath.removeAt(numPhotos)

        }
    }

    // Saves a beer to the database from the input fields
    private fun saveBeer() {
        // Read from input fields. Use trim to eliminate excess white space.
        val beerName = nameEdit.text.toString().trim { it <= ' ' }
        var beerType = typeSpinner.selectedItem.toString()
        // If beerType is other set beerType to EditText string
        if (beerType == getString(R.string.other)) beerType = typeEdit.text.toString().trim { it <= ' ' }
        val ratingDouble = ratingSpinner.selectedItem.toString().toDoubleOrNull()
        var percentage = percentageEdit.text.toString().toDoubleOrNull()
        var bitterness = bitternessEdit.text.toString().toIntOrNull()
        var breweryName = breweryNameEdit.text.toString().trim { it <= ' ' }
        var city = cityEdit.text.toString().trim { it <= ' ' }
        val state = stateEdit.text.toString().trim { it <= ' ' }
        var country = countryEdit.text.toString().trim { it <= ' ' }
        val comments = commentsEdit.text.toString().trim { it <= ' ' }
        val year = datePicker.year
        val month = datePicker.month
        val day = datePicker.dayOfMonth
        val date = year.toString() + "-" + month + "-" + day

        // Check that BeerName has an entry. Popup a toast to alert user.
        if (beerName.isEmpty()) {
            Toast.makeText(activity, "Beer must have a name!", Toast.LENGTH_SHORT).show()
            return
        }

        // Set default values if fields are empty
        if (breweryName.isEmpty()) breweryName = getString(R.string.default_field_string)
        if (city.isEmpty()) city = getString(R.string.default_field_string)
        if (country.isEmpty()) country = getString(R.string.default_field_string)
        if (bitterness == null) bitterness = -1
        if (percentage == null) percentage = -1.0
        if (beerType == "---") beerType = getString(R.string.default_field_string)
        val rating = if (ratingDouble == null) 0 else (2*ratingDouble).toInt()

        val beer = Beer(null, beerName, "", beerType, rating, percentage, bitterness, date, comments, breweryName, country, city, state)


        Log.d(LOG_TAG, "name: " + beerName + ", rating: " + rating + ", percentage: " + percentage + ", bitterness: "
                + bitterness + ", breweryName: " + breweryName + ", date: " + date)
        Log.d(LOG_TAG, "IMAGES: " + Utilities.listToString(photoPath))

//        val replyIntent = Intent()
//        replyIntent.putExtra(EXTRA_REPLY, beer)
//        setResult(RESULT_OK, replyIntent)

//         Determine if this is a new or existing Beer
        // Add a new beer
        if (beerId == -1L) viewModel.insertBeer(beer)
        else viewModel.updateBeer(beer)

//        if (beerId == null) {
//            // Add a new beer
//            val newUri = activity.contentResolver.insert(BeerEntry.CONTENT_URI, values)
//            // Show a toast message depending on whether or not the insertion was successful
//            if (newUri == null)
//                Toast.makeText(activity, "Insert new beer failed.", Toast.LENGTH_SHORT).show()
//            else
//                Toast.makeText(activity, "New beer successfully added to database.", Toast.LENGTH_SHORT).show()
//        } else {
//            // This is an existing beer entry, so update the database with the new values
//            val rowsAffected = activity.contentResolver.update(beerId!!, values, null, null)
//            // Show a toast to let the user know whether the beer was updated successfully
//            if (rowsAffected == 0)
//                Toast.makeText(activity, "Update beer data failed.", Toast.LENGTH_SHORT).show()
//            else
//                Toast.makeText(activity, "Beer data updated successfully.", Toast.LENGTH_SHORT).show()
//        }
    }

    /**
     * Deletes the current beer from the database
     */
    private fun deleteBeer() {
        // Only perform the deletion if it is an existing beer
        if (beerId >= 0) {
            // TODO delete images associated with the beer item.
            for (fileName in photoPath) {
                // Delete image
                val imageFile = File(fileName)
                val deleteSuccessful = imageFile.delete()
                if (deleteSuccessful)
                    Log.v(LOG_TAG, "Delete successful: $fileName")
                else
                    Log.v(LOG_TAG, "Delete failed: $fileName")
                // Delete small thumbnail
//                val thumbFileName = Utilities.thumbFilePath(fileName, Companion.getTHUMB_SMALL_W())
//                val thumbFile = File(thumbFileName)
//                val thumbDeleteSuccessful = thumbFile.delete()
//                if (thumbDeleteSuccessful)
//                    Log.v(LOG_TAG, "Thumbnail delete successful: $thumbFileName")
//                else
//                    Log.v(LOG_TAG, "Thumbnail delete failed: $thumbFileName")
//                // Delete large thumbnail
//                val thumbLargeFileName = Utilities.thumbFilePath(fileName, Companion.getTHUMB_LARGE_W())
//                val thumbLargeFile = File(thumbLargeFileName)
//                val thumbLargeDeleteSuccessful = thumbFile.delete()
//                if (thumbLargeDeleteSuccessful)
//                    Log.v(LOG_TAG, "Thumbnail delete successful: $thumbLargeFile")
//                else
//                    Log.v(LOG_TAG, "Thumbnail delete failed: $thumbLargeFileName")
            }
            // Call the content resolver to delete the beer from database
//            val rowsDeleted = activity.contentResolver.delete(beerId, null, null)
//            // Show a toast message depending on whether or not the delete was successfull
//            if (rowsDeleted == 0)
//                Toast.makeText(activity, getString(R.string.editor_delete_beer_failed), Toast.LENGTH_SHORT).show()
//            else
//                Toast.makeText(activity, getString(R.string.editor_delete_beer_successful), Toast.LENGTH_SHORT).show()
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
        if (beerId == -1L) {
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


//    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor?) {
//        // Bail early if the cursor is null or there is less than 1 row in the cursor
//        if (cursor == null || cursor.count < 1) return
//
//        // Move to the first row of the cursor (should be only row) and read data from it
//        if (cursor.moveToFirst()) {
//            // Get the data columns we're interested in
//            val beerNameColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_NAME)
//            val beerTypeColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_TYPE)
//            val beerIBUColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_IBU)
//            val breweryNameColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BREWERY_NAME)
//            val cityColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BREWERY_CITY)
//            val stateColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BREWERY_STATE)
//            val countryColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BREWERY_COUNTRY)
//            val dateColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_DATE)
//            val percentageColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_PERCENTAGE)
//            val ratingColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_RATING)
//            val commentsColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_COMMENTS)
//            val imageColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_PHOTO)
//
//            // Read the beer attributes from the cursor for the current beer
//            val beerName = cursor.getString(beerNameColumn)
//            val beerType = cursor.getString(beerTypeColumn)
//            val bitterness = cursor.getInt(beerIBUColumn)
//            var breweryName = cursor.getString(breweryNameColumn)
//            var city = cursor.getString(cityColumn)
//            val state = cursor.getString(stateColumn)
//            var country = cursor.getString(countryColumn)
//            val date = cursor.getString(dateColumn)
//            val comments = cursor.getString(commentsColumn)
//            val percentage = cursor.getDouble(percentageColumn)
//            val rating = cursor.getInt(ratingColumn).toDouble() / 2
//            val imageStrings = cursor.getString(imageColumn)
//            photoPath = Utilities.stringToList(imageStrings)
//
//            // TODO Handle default values
//
//            // Update the edit text views with the attributes for the current beer
//            this.nameEdit!!.setText(beerName)
//            if (breweryName == BeerEntry.DEAULT_STRING) breweryName = ""
//            breweryNameEdit!!.setText(breweryName)
//            if (city == BeerEntry.DEAULT_STRING) city = ""
//            cityEdit!!.setText(city)
//            stateEdit!!.setText(state)
//            if (country == BeerEntry.DEAULT_STRING) country = ""
//            countryEdit!!.setText(country)
//            commentsEdit!!.setText(comments)
//            if (percentage < 0)
//                percentageEdit!!.setText("")
//            else
//                percentageEdit!!.setText(percentage.toString())
//            if (bitterness < 0)
//                bitternessEdit!!.setText("")
//            else
//                bitternessEdit!!.setText(bitterness.toString())
//
//            // Update the spinners
//            // If beertype equals "unknown" set the spinner to ---
//            if (beerType == BeerEntry.DEAULT_STRING) {
//                typeSpinner!!.setSelection(0)
//            } else {
//                val typeArray = Arrays.asList(*resources.getStringArray(R.array.array_beer_type_options))
//                val itemIndex = typeArray.indexOf(beerType)
//                // If itemIndex >=0 set the selection to that item. Otherwise the string is not in the spinner
//                // So set the spinner to other, show the edit text field and set the text.
//                if (itemIndex >= 0) {
//                    typeSpinner!!.setSelection(itemIndex)
//                } else {
//                    typeSpinner!!.setSelection(typeArray.indexOf(getString(R.string.other)))
//                    typeEdit!!.visibility = View.VISIBLE
//                    typeEdit!!.setText(beerType)
//                }
//                //                Log.d(LOG_TAG, "type " + beerType + " " + typeArray.indexOf(beerType));
//
//            }// If beertype equals something that is not
//            val ratingArray = Arrays.asList(*resources.getStringArray(R.array.array_rating_options))
//            ratingSpinner!!.setSelection(ratingArray.indexOf(rating.toString()))
//            //            Log.d(LOG_TAG, "rating " + String.valueOf(rating));
//
//            // Update the date picker
//            val dateArray = date.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//            datePicker!!.updateDate(Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1]), Integer.parseInt(dateArray[2]))
//
//            // Update the image view
////            Utilities.setThumbnailFromWidth(beerImageView!!, photoPath[0], Companion.getTHUMB_LARGE_W())
//        }
//    }
//

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

        const val EXTRA_REPLY = "com.wordpress.excelenteadventura.beerjournal.REPLY"

        private val LOG_TAG = AddBeerActivity::class.java.simpleName

        internal const val REQUEST_IMAGE_CAPTURE = 1
    }
}
