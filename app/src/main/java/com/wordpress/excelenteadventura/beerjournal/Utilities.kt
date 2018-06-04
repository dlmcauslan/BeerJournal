package com.wordpress.excelenteadventura.beerjournal

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.decodeFile
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by DLMcAuslan on 1/4/2017.
 * Utilities class that contains some helper functions for managing photos,
 * file input-output etc
 */

object Utilities {

    private val LOG_TAG = Utilities::class.java.simpleName

    /**
     * Create a temporary image file to save the image taken by the camera app in.
     * @param context
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "BeerJournal" + timeStamp + "_"
        //        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

//    /**
//     * Loads an image from the phones storage
//     * @param context
//     * @param imageName - filename of image to load
//     * @return - the loaded image.
//     */
//    fun loadImage(context: Context, imageName: String): Bitmap? {
//        var bitmap: Bitmap? = null
//        try {
//            val fiStream = FileInputStream(File(imageName))
//            bitmap = BitmapFactory.decodeStream(fiStream)
//            fiStream.close()
//        } catch (e: Exception) {
//            Log.e(LOG_TAG, "Error loading image.")
//        }
//        return bitmap
//    }
//
//    fun setImage(imageView: ImageView, photoPath: String) {
//        // Get the dimensions of the View
//        val targetW = imageView.width
//        val targetH = imageView.height
//
//        // Get the dimensions of the bitmap
//        val bmOptions = BitmapFactory.Options()
//        bmOptions.inJustDecodeBounds = true
//        BitmapFactory.decodeFile(photoPath, bmOptions)
//        val photoW = bmOptions.outWidth
//        val photoH = bmOptions.outHeight
//
//        // Determine how much to scale down the image for the editor screen.
////        Log.d(LOG_TAG, "w: " + targetW + ", h: " + targetH + ", w2: " + photoW + ", h2: " + photoH);
//        // To stop crashing do a default scaling of 8 if either targetW or targetH are 0
//        var scaleFactor = 8
//        if (targetH > 0 && targetW > 0) scaleFactor = Math.min(photoW/targetW, photoH/targetH)
//        Log.d(LOG_TAG, "Scale factor: " + scaleFactor)
//
//        // Decode the image file into a Bitmap sized to fill the View
//        bmOptions.inJustDecodeBounds = false
//        bmOptions.inSampleSize = scaleFactor
//        bmOptions.inPurgeable = true
//
//        val bitmap = BitmapFactory.decodeFile(photoPath, bmOptions)
//        imageView.setImageBitmap(bitmap)
//    }


    /**
     * From the provided photoPath and thumbNailWidth, scales the photo and creates a thumbnail
     * image.
     * @param photoPath
     * @param desiredThumbnailWidth
     */
    fun createThumbnail(photoPath: String, desiredThumbnailWidth: Int) {
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        decodeFile(photoPath, bmOptions)
        val photoW = bmOptions.outWidth

        // Get the scaleFactor for the thumbnail image (for main screen not editor screen)
        val thumbScaleFactor = photoW / desiredThumbnailWidth
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = thumbScaleFactor
        bmOptions.inPurgeable = true
        // Create thumbnail image
        val thumbnailImage = decodeFile(photoPath, bmOptions)
        saveThumbnail(thumbnailImage, photoPath, desiredThumbnailWidth)
    }

    // Saves the thumbnail created in createThumbnail
    private fun saveThumbnail(image: Bitmap, photoPath: String, desiredWidth: Int) {
        // Gets filename for the thumbnail
        val imageName = thumbFilePath(photoPath, desiredWidth)
        Log.d(LOG_TAG, "thumbname = $imageName")
        // Save the thumbnail as a jpg
        try {
            val foStream = FileOutputStream(File(imageName))
            image.compress(Bitmap.CompressFormat.JPEG, 100, foStream)
            foStream.close()
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error saving image.")
            e.printStackTrace()
        }

    }

    // Creates the filename of the thumbnail from the main image filename and the thumbnail width
    fun thumbFilePath(filePath: String, desiredWidth: Int): String {
        return filePath.split("\\.jpg".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0] + "_thumb" + desiredWidth + ".jpg"
    }

    // Using the photoPath sets a thumbnail image to the provided imageView.
    fun setThumbnailFromWidth(imageView: ImageView, photoPath: String, desiredWidth: Int) {
        val thumbPath = thumbFilePath(photoPath, desiredWidth)
        Log.d(LOG_TAG, "thumbname Set = $thumbPath")
        val bitmap = decodeFile(thumbPath)
        imageView.setImageBitmap(bitmap)
    }

    /**
     * Concatenates a list of string objects into a single string object, where individual strings
     * are separated by commas. This allows the list to be stored in the database.
     * @param list
     * @return
     */
    fun listToString(list: List<String>): String {
        var listString = ""
        // Loop over list except last element
        for (i in list.indices) {
            listString += list[i]
            // Don't add the separator on the last item
            if (i < list.size - 1) listString += ","
        }
        // Add last element
        return listString
    }

    /**
     * Takes a string that is made up of strings concatenated with a comma (see listToString) and
     * converts them back to an ArrayList.
     * @param listString
     * @return
     */
    fun stringToList(listString: String): ArrayList<String> {
        return ArrayList(Arrays.asList(*listString.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()))
    }
}
