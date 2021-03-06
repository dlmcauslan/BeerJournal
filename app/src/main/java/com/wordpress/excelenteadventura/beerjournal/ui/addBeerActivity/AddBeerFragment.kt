package com.wordpress.excelenteadventura.beerjournal.ui.addBeerActivity


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
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
import com.wordpress.excelenteadventura.beerjournal.databinding.FragmentAddBeerBinding
import com.wordpress.excelenteadventura.beerjournal.ui.imagesActivity.ImagesActivity
import com.wordpress.excelenteadventura.beerjournal.ui.mainActivity.THUMB_LARGE_W
import com.wordpress.excelenteadventura.beerjournal.ui.mainActivity.THUMB_SMALL_W
import com.wordpress.excelenteadventura.beerjournal.visible
import kotlinx.android.synthetic.main.fragment_add_beer.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

internal const val REQUEST_IMAGE_CAPTURE = 1
internal const val PHOTOS_EXTRA = "com.wordpress.excelenteadventura.beerjournal.photosExtra"
internal const val BEER_NAME_EXTRA = "com.wordpress.excelenteadventura.beerjournal.beerNameExtra"

/**
 * A simple [Fragment] subclass.
 */
class AddBeerFragment : Fragment() {

    private val LOG_TAG = AddBeerActivity::class.java.simpleName

    private lateinit var act: Activity

    // View Model
    private lateinit var viewModel: AddBeerViewModel

    // Current beer
    private var currentBeer: Beer? = null

    // Data edit fields
    private lateinit var typeSpinner: Spinner
    private lateinit var typeEdit: EditText
    private lateinit var ratingSpinner: Spinner
    private lateinit var dateText: TextView
    private lateinit var beerImageView: ImageView
    private lateinit var defaultImageView: TextView

    // An ArrayList to hold Strings that contain the paths to the photos.
    private var photoPath = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        act = activity ?: return null

        // Inflate the layout for this fragment
        val binding = FragmentAddBeerBinding.inflate(inflater, container, false)
        typeSpinner = binding.spinnerBeerType
        typeEdit = binding.editBeerType
        ratingSpinner = binding.spinnerBeerRating
        dateText = binding.editDate
        beerImageView = binding.imageBeerPhoto
        defaultImageView = binding.defaultImage

        // Setup View Model
        val factory = InjectorUtils.provideAddBeerViewModelFactory(act)
        viewModel = ViewModelProviders.of(this, factory).get(AddBeerViewModel::class.java)
        viewModel.currentBeer.observe(this,
                Observer { beer ->
                    currentBeer = beer
                    binding.beer = beer
                    populateUI(beer)
                    setFragmentTitle(beer)
                }
        )

        // This line enables the fragment to handle menu events
        setHasOptionsMenu(true)

        // On click listener for beer type spinner to show beer type edit text if other is selected
        typeSpinner.onItemSelectedListener = typeSpinnerItemSelectListener

        // On click listener for Add Photo text
        binding.addBeerTakePhoto.setOnClickListener { startCameraIntent() }

        // On click listener for photo to open imagesActivity
        beerImageView.setOnClickListener( beerImageClickListener )
        defaultImageView.setOnClickListener{ startCameraIntent() }

        return binding.root
    }

    private fun setFragmentTitle(beer: Beer?) {
        activity?.title = getString( if (beer == null) R.string.add_beer_title else R.string.edit_beer_title )
    }

    private val typeSpinnerItemSelectListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(arg0: AdapterView<*>, arg1: View?, arg2: Int, arg3: Long) {
            val item = typeSpinner.selectedItem.toString()
            Log.i("Selected item : ", item)
            typeEdit.visible(item == getString(R.string.other))
        }
        override fun onNothingSelected(arg0: AdapterView<*>) { }

    }

    private fun populateUI(beer: Beer?) {
        beer?.let {
            // Edit texts are updated using databinding
            // Update the spinners
            // If beertype equals "unknown" set the spinner to ---
            val beerType = beer.type
            if (beerType == "") {
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
                    typeEdit.visible()
                    typeEdit.setText(beerType)
                }
            }
            val ratingArray = Arrays.asList(*resources.getStringArray(R.array.array_rating_options))
            val rating = beer.rating.toDouble() / 2
            ratingSpinner.setSelection(ratingArray.indexOf(rating.toString()))

            // Update the image view
            photoPath = Utilities.stringToList(it.photoLocation)
            beerImageView.visible(photoPath.isNotEmpty())
            add_beer_take_photo.visible(photoPath.isNotEmpty())
            defaultImageView.visible(photoPath.isEmpty())
            if (photoPath.isNotEmpty()) Utilities.setThumbnailFromWidth(beerImageView, photoPath[0], THUMB_LARGE_W)
        }

        // Update the date picker
        updateDatePicker(beer)
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateDatePicker(beer: Beer?) {
        val date = Calendar.getInstance()
        beer?.let {
            val dateArray = beer.date.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            date.set(dateArray[2].toInt(), dateArray[1].toInt()-1, dateArray[0].toInt())
        }
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val dateString = dateFormat.format(date.time)
        dateText.text = dateString
        dateText.setOnClickListener {
            DatePickerDialog(context,
                    DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                        date.set(year, monthOfYear, dayOfMonth)
                        dateText.text = dateFormat.format(date.time)
                    },
                    // set DatePickerDialog to point to today's date when it loads up
                    date.get(Calendar.YEAR),
                    date.get(Calendar.MONTH),
                    date.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private val beerImageClickListener = { _: View ->
        if (photoPath.isEmpty() || photoPath[0].isEmpty()) startCameraIntent()
        else if (photoPath.size == 1) openImageInGallery()
        else goToImagesActivity()
    }

    private fun openImageInGallery() {
        val file = File(photoPath[0])
        val uri = Uri.fromFile(file)
        val intent = Intent(android.content.Intent.ACTION_VIEW)
        var mime: String? = "*/*"
        val mimeTypeMap = MimeTypeMap.getSingleton()
        if (mimeTypeMap.hasExtension(getFileExtensionFromUrl(uri.toString()))) {
            mime = mimeTypeMap.getMimeTypeFromExtension(getFileExtensionFromUrl(uri.toString()))
        }
        val apkUri = FileProvider.getUriForFile(act, act.applicationContext.packageName + ".provider", file)
        intent.setDataAndType(apkUri, mime)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }

    private fun goToImagesActivity() {
        val intent = Intent(activity, ImagesActivity::class.java)
        intent.putStringArrayListExtra(PHOTOS_EXTRA, photoPath)
        intent.putExtra(BEER_NAME_EXTRA, edit_beer_name.text.toString().trim { it <= ' ' })
        startActivity(intent)
    }

    private fun startCameraIntent() {
        // Start an intent to open the camera app
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(activity?.packageManager) != null) {
            // Create the file where the photo should go
            var photoFile: File? = null
            try {
                photoFile = Utilities.createImageFile(act)
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
                val photoURI = FileProvider.getUriForFile(act, act.applicationContext.packageName + ".provider", photoFile)
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
            beerImageView.visible()
            add_beer_take_photo.visible()
            defaultImageView.visible(false)
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
        val beerName = edit_beer_name.text.toString().trim { it <= ' ' }
        var beerType = typeSpinner.selectedItem.toString()
        // If beerType is other set beerType to EditText string
        if (beerType == getString(R.string.other)) beerType = typeEdit.text.toString().trim { it <= ' ' }
        val ratingDouble = ratingSpinner.selectedItem.toString().toDoubleOrNull()
        var percentage = edit_beer_percentage.text.toString().toDoubleOrNull()
        var bitterness = edit_beer_bitterness.text.toString().toIntOrNull()
        val breweryName = edit_brewery_name.text.toString().trim { it <= ' ' }
        val city = edit_city.text.toString().trim { it <= ' ' }
        val state = edit_state.text.toString().trim { it <= ' ' }
        val country = edit_country.text.toString().trim { it <= ' ' }
        val comments = edit_beer_comments.text.toString().trim { it <= ' ' }
        val date = dateText.text.toString()

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

        val beer = Beer(currentBeer?.id, beerName, Utilities.listToString(photoPath), beerType, rating, percentage, bitterness, date, comments, breweryName, country, city, state)

        Log.d(LOG_TAG, "name: " + beerName + ", rating: " + rating + ", percentage: " + percentage + ", bitterness: "
                + bitterness + ", breweryName: " + breweryName + ", date: " + date)
        Log.d(LOG_TAG, "IMAGES: " + Utilities.listToString(photoPath))

        // Insert/update the beer into the database
        viewModel.saveBeer(beer)
        activity?.finish()
    }

    /**
     * Deletes the current beer from the database
     */
    private fun deleteBeer() {
        // Only perform the deletion if it is an existing beer
        currentBeer?.let {
            viewModel.deleteBeer(it)
        }
        // Close the activity
        activity?.finish()
    }

    // Options menu code
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        activity?.menuInflater?.inflate(R.menu.menu_editor, menu)
    }

    // This allows some menu items to be hidden or made visible.
    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        // If it is a new beer, hide the Delete menu item.
        if (currentBeer == null) {
            val menuItem = menu?.findItem(R.id.action_delete)
            menuItem?.isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // Perform different activities based on which item the user clicks
        when (item?.itemId) {
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
                activity?.finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Prompts the user to make sure they want to delete the beer.
     */
    private fun showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message. And click listeners
        // for the positive and negative buttons on the dialog.
        val builder = AlertDialog.Builder(act)
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
}

