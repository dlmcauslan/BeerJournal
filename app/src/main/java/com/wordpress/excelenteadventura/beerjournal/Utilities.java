package com.wordpress.excelenteadventura.beerjournal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by DLMcAuslan on 1/4/2017.
 * Utilities class that contains some helper functions for managing photos,
 * file input-output etc
 */

public final class Utilities {

    private static final String LOG_TAG = Utilities.class.getSimpleName();

    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "BeerJournal" + timeStamp + "_";
//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        return image;
    }

    /**
     * Loads an image from the phones storage
     * @param context
     * @param imageName - filename of image to load
     * @return - the loaded image.
     */
    public static Bitmap loadImage(Context context, String imageName) {
        Bitmap bitmap = null;
//        FileInputStream fiStream;
        try {
//            fiStream = context.openFileInput(imageName);
            FileInputStream fiStream = new FileInputStream (new File(imageName));
            bitmap = BitmapFactory.decodeStream(fiStream);
            fiStream.close();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error loading image.");
            e.printStackTrace();
        }
        return bitmap;
    }


    public static void setImage(ImageView imageView, String photoPath) {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        Log.d(LOG_TAG, "w: " + targetW + ", h: " + targetH + ", w2: " + photoW + ", h2: " + photoH);
        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        Log.d(LOG_TAG, "Scale factor: " + scaleFactor);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    public static void setThumbnailFromWidth(ImageView imageView, String photoPath, int desiredWidth) {
        // Get the dimensions of the View
//        int targetW = imageView.getWidth();
//        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;

//        Log.d(LOG_TAG, "w: " + targetW + ", h: " + targetH + ", w2: " + photoW + ", h2: " + photoH);
        // Determine how much to scale down the image
//        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        int scaleFactor = photoW/desiredWidth;
//        int scaleFactor = 4;
        Log.d(LOG_TAG, "Scale factor: " + scaleFactor);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
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
