package com.wordpress.excelenteadventura.beerjournal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static android.graphics.BitmapFactory.decodeFile;

/**
 * Created by DLMcAuslan on 1/4/2017.
 * Utilities class that contains some helper functions for managing photos,
 * file input-output etc
 */

public final class Utilities {

    private static final String LOG_TAG = Utilities.class.getSimpleName();

    /**
     * Create a temporary image file to save the image taken by the camera app in.
     * @param context
     * @return
     * @throws IOException
     */
    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "BeerJournal" + timeStamp + "_";
//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        return image;
    }

//    /**
//     * Loads an image from the phones storage
//     * @param context
//     * @param imageName - filename of image to load
//     * @return - the loaded image.
//     */
//    public static Bitmap loadImage(Context context, String imageName) {
//        Bitmap bitmap = null;
//        try {
//            FileInputStream fiStream = new FileInputStream (new File(imageName));
//            bitmap = BitmapFactory.decodeStream(fiStream);
//            fiStream.close();
//        } catch (Exception e) {
//            Log.e(LOG_TAG, "Error loading image.");
//            e.printStackTrace();
//        }
//        return bitmap;
//    }


//    public static void setImage(ImageView imageView, String photoPath) {
//        // Get the dimensions of the View
//        int targetW = imageView.getWidth();
//        int targetH = imageView.getHeight();
//
//        // Get the dimensions of the bitmap
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(photoPath, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//        // Determine how much to scale down the image for the editor screen.
////        Log.d(LOG_TAG, "w: " + targetW + ", h: " + targetH + ", w2: " + photoW + ", h2: " + photoH);
//        // To stop crashing do a default scaling of 8 if either targetW or targetH are 0
//        int scaleFactor = 8;
//        if (targetH > 0 && targetW > 0) scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//        Log.d(LOG_TAG, "Scale factor: " + scaleFactor);
//
//        // Decode the image file into a Bitmap sized to fill the View
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;
//
//        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
//        imageView.setImageBitmap(bitmap);
//    }



    /**
     * From the provided photoPath and thumbNailWidth, scales the photo and creates a thumbnail
     * image.
     * @param photoPath
     * @param desiredThumbnailWidth
     */
    public static void createThumbnail(String photoPath, int desiredThumbnailWidth) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;

        // Get the scaleFactor for the thumbnail image (for main screen not editor screen)
        int thumbScaleFactor = photoW / desiredThumbnailWidth;
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = thumbScaleFactor;
        bmOptions.inPurgeable = true;
        // Create thumbnail image
        Bitmap thumbnailImage = decodeFile(photoPath, bmOptions);
        saveThumbnail(thumbnailImage, photoPath, desiredThumbnailWidth);
    }

    // Saves the thumbnail created in createThumbnail
    private static void saveThumbnail(Bitmap image, String photoPath, int desiredWidth) {
        // Gets filename for the thumbnail
        String imageName = thumbFilePath(photoPath, desiredWidth);
        Log.d(LOG_TAG, "thumbname = " + imageName);
        // Save the thumbnail as a jpg
        try {
            FileOutputStream foStream = new FileOutputStream (new File(imageName));
            image.compress(Bitmap.CompressFormat.JPEG, 100, foStream);
            foStream.close();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error saving image.");
            e.printStackTrace();
        }
    }

    // Creates the filename of the thumbnail from the main image filename and the thumbnail width
    public static String thumbFilePath(String filePath, int desiredWidth) {
        return filePath.split("\\.jpg")[0] + "_thumb" + desiredWidth + ".jpg";
    }

    // Using the photoPath sets a thumbnail image to the provided imageView.
    public static void setThumbnailFromWidth(ImageView imageView, String photoPath, int desiredWidth) {
        String thumbPath = thumbFilePath(photoPath,desiredWidth);
        Log.d(LOG_TAG, "thumbname Set = " + thumbPath);
        Bitmap bitmap = decodeFile(thumbPath);
        imageView.setImageBitmap(bitmap);
    }

    /**
     * Concatenates a list of string objects into a single string object, where individual strings
     * are separated by commas. This allows the list to be stored in the database.
     * @param list
     * @return
     */
    public static String listToString(List<String> list) {
        String listString = "";
        // Loop over list except last element
        for (int i = 0; i < list.size(); i++) {
            listString += list.get(i);
            // Don't add the separator on the last item
            if (i < list.size()-1) listString += ",";
        }
        // Add last element
        return listString;
    }

    /**
     * Takes a string that is made up of strings concatenated with a comma (see listToString) and
     * converts them back to an ArrayList.
     * @param listString
     * @return
     */
    public static ArrayList<String> stringToList(String listString) {
        return new ArrayList<String>(Arrays.asList(listString.split(",")));
    }
}
