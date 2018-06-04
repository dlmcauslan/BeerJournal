package com.wordpress.excelenteadventura.beerjournal.ui.addBeerActivity


import android.app.Activity.RESULT_OK
import android.arch.lifecycle.Observer
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
import android.webkit.MimeTypeMap.getFileExtensionFromUrl
import android.widget.*
import com.wordpress.excelenteadventura.beerjournal.InjectorUtils
import com.wordpress.excelenteadventura.beerjournal.R
import com.wordpress.excelenteadventura.beerjournal.Utilities
import com.wordpress.excelenteadventura.beerjournal.database.Beer
import com.wordpress.excelenteadventura.beerjournal.database.BeerContract
import com.wordpress.excelenteadventura.beerjournal.ui.imagesActivity.ImagesActivity
import com.wordpress.excelenteadventura.beerjournal.ui.mainActivity.MainFragment.Companion.THUMB_LARGE_W
import com.wordpress.excelenteadventura.beerjournal.ui.mainActivity.MainFragment.Companion.THUMB_SMALL_W
import kotlinx.android.synthetic.main.fragment_add_beer.view.*
import java.io.File
import java.io.IOException
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class AddBeerFragment : Fragment() {

    private val LOG_TAG = AddBeerActivity::class.java.simpleName

    // View Model
    private lateinit var viewModel: AddBeerViewModel

    // Current beer
    private var currentBeer: Beer? = null

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
        viewModel.currentBeer.observe(this,
                Observer { beer ->
                    currentBeer = beer
                    populateUI(beer) }
        )

//        Log.v(LOG_TAG, "pixels: " + Companion.getTHUMB_SMALL_W() + " " + Companion.getTHUMB_LARGE_W())

        // This line enables the fragment to handle menu events
        setHasOptionsMenu(true)
        // If the intent does not contain a Beer content URI then we are adding a new beer
        if (currentBeer == null) {
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
        beerImageView.setOnClickListener( beerImageClickListener )

        return fragment
    }

    private fun populateUI(beer: Beer?) {
//            // TODO Handle default values
        beer?.let {
            // Update the edit text views with the attributes for the current beer
            nameEdit.setText(beer.name)
            breweryNameEdit.setText(beer.brewery)
            cityEdit.setText(beer.city)
            stateEdit.setText(beer.state)
            countryEdit.setText(beer.country)
            commentsEdit.setText(beer.comments)
            val percentage = beer.percentage
            if (percentage < 0)
                percentageEdit.setText("")
            else
                percentageEdit.setText(percentage.toString())
            val bitterness = beer.bitterness
            if (bitterness < 0)
                bitternessEdit.setText("")
            else
                bitternessEdit.setText(bitterness.toString())

            // Update the spinners
            // If beertype equals "unknown" set the spinner to ---
            val beerType = beer.type
            if (beerType == BeerContract.BeerEntry.DEAULT_STRING) {
                typeSpinner.setSelection(0)
            } else {
                val typeArray = Arrays.asList(*resources.getStringArray(R.array.array_beer_type_options))
                val itemIndex = typeArray.indexOf(beerType)
                // If itemIndex >=0 set the selection to that item. Otherwise the string is not in the spinner
                // So set the spinner to other, show the edit text field and set the text.
                if (itemIndex >= 0) {
                    typeSpinner.setSelection(itemIndex)
                } else {
                    typeSpinner.setSelection(typeArray.indexOf(getString(R.string.other)))
                    typeEdit.visibility = View.VISIBLE
                    typeEdit.setText(beerType)
                }
                //                Log.d(LOG_TAG, "type " + beerType + " " + typeArray.indexOf(beerType));

            }// If beertype equals something that is not
            val ratingArray = Arrays.asList(*resources.getStringArray(R.array.array_rating_options))
            val rating = beer.rating.toDouble() / 2
            ratingSpinner.setSelection(ratingArray.indexOf(rating.toString()))
            //            Log.d(LOG_TAG, "rating " + String.valueOf(rating));

            // Update the date picker
            val dateArray = beer.date.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            datePicker.updateDate(Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1]), Integer.parseInt(dateArray[2]))

            // Update the image view
            photoPath = Utilities.stringToList(it.photoLocation)
            if (photoPath.isNotEmpty()) {
                Utilities.setThumbnailFromWidth(beerImageView, photoPath[0], THUMB_LARGE_W)
            }
        }
    }

    private val beerImageClickListener = { _: View ->
        val intent: Intent
        // If the photo is null or empty then don't pass the intent
        if (photoPath.isEmpty() || photoPath[0].isEmpty()) {
            startCameraIntent()
        } else if (photoPath.size == 1) {
            // Opens the image in gallery
            val file = File(photoPath[0])
            val uri = Uri.fromFile(file)
            intent = Intent(android.content.Intent.ACTION_VIEW)
            var mime: String? = "*/*"
            val mimeTypeMap = MimeTypeMap.getSingleton()
            if (mimeTypeMap.hasExtension(getFileExtensionFromUrl(uri.toString()))) {
                mime = mimeTypeMap.getMimeTypeFromExtension(getFileExtensionFromUrl(uri.toString()))
            }
            val apkUri = FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", file)
            intent.setDataAndType(apkUri, mime)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            intent.setDataAndType(uri, mime)
            startActivity(intent)
        } else {
            // Create new intent to go to the Images Activity
            intent = Intent(activity, ImagesActivity::class.java)
            intent.putStringArrayListExtra("photosExtra", photoPath)
            intent.putExtra("nameEdit", nameEdit.text.toString().trim { it <= ' ' })
            startActivity(intent)
        }
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
                val photoURI = FileProvider.getUriForFile(activity, context.applicationContext.packageName + ".provider", photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    // Code that gets the result of the photograph and adds it to the imageView.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val numPhotos = photoPath.size - 1
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//             Create a small and large thumbnail of the captured image, then set the large
//             thumbnail to the imageview.
            Utilities.createThumbnail(photoPath[numPhotos], THUMB_SMALL_W)
            Utilities.createThumbnail(photoPath[numPhotos], THUMB_LARGE_W)
            Utilities.setThumbnailFromWidth(beerImageView, photoPath[0], THUMB_LARGE_W)
        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            // Otherwise remove the path because the photo was not saved
            Log.d(LOG_TAG, "resultCode not good, path is" + photoPath[numPhotos])
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
        val breweryName = breweryNameEdit.text.toString().trim { it <= ' ' }
        val city = cityEdit.text.toString().trim { it <= ' ' }
        val state = stateEdit.text.toString().trim { it <= ' ' }
        val country = countryEdit.text.toString().trim { it <= ' ' }
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
        if (bitterness == null) bitterness = -1
        if (percentage == null) percentage = -1.0
        if (beerType == "---") beerType = ""
        val rating = if (ratingDouble == null) 0 else (2*ratingDouble).toInt()

        val beer = Beer(null, beerName, Utilities.listToString(photoPath), beerType, rating, percentage, bitterness, date, comments, breweryName, country, city, state)

        Log.d(LOG_TAG, "name: " + beerName + ", rating: " + rating + ", percentage: " + percentage + ", bitterness: "
                + bitterness + ", breweryName: " + breweryName + ", date: " + date)
        Log.d(LOG_TAG, "IMAGES: " + Utilities.listToString(photoPath))

//         Determine if this is a new or existing Beer
        if (currentBeer == null) {
            viewModel.insertBeer(beer)
        } else {
            beer.id = currentBeer?.id
            viewModel.updateBeer(beer)
        }
        activity.finish()
    }

    /**
     * Deletes the current beer from the database
     */
    private fun deleteBeer() {
        // Only perform the deletion if it is an existing beer
        currentBeer?.let {
            // TODO delete images associated with the beer item.
            for (fileName in photoPath) {
                // Delete image
                val imageFile = File(fileName)
                val deleteSuccessful = imageFile.delete()
                if (deleteSuccessful)
                    Log.d(LOG_TAG, "Delete successful: $fileName")
                else
                    Log.d(LOG_TAG, "Delete failed: $fileName")
                // Delete small thumbnail
                val thumbFileName = Utilities.thumbFilePath(fileName, THUMB_SMALL_W)
                val thumbFile = File(thumbFileName)
                val thumbDeleteSuccessful = thumbFile.delete()
                if (thumbDeleteSuccessful)
                    Log.d(LOG_TAG, "Thumbnail delete successful: $thumbFileName")
                else
                    Log.d(LOG_TAG, "Thumbnail delete failed: $thumbFileName")
                // Delete large thumbnail
                val thumbLargeFileName = Utilities.thumbFilePath(fileName, THUMB_LARGE_W)
                val thumbLargeFile = File(thumbLargeFileName)
                val thumbLargeDeleteSuccessful = thumbFile.delete()
                if (thumbLargeDeleteSuccessful)
                    Log.d(LOG_TAG, "Thumbnail delete successful: $thumbLargeFile")
                else
                    Log.d(LOG_TAG, "Thumbnail delete failed: $thumbLargeFileName")
            }
            viewModel.deleteBeer(it)
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
        if (currentBeer == null) {
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
        internal const val REQUEST_IMAGE_CAPTURE = 1
    }
}

