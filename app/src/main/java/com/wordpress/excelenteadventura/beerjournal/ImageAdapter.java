package com.wordpress.excelenteadventura.beerjournal;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import static com.wordpress.excelenteadventura.beerjournal.MainFragment.THUMB_LARGE_W;

/**
 * Adaptor class that sets images to the gridview in the ImagesFragment.
 * Created by DLMcAuslan on 1/5/2017.
 */

public class ImageAdapter extends BaseAdapter {

    private static final String LOG_TAG = ImageAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<String> mImagesPath;

    public ImageAdapter(Context context, ArrayList<String> imagesPath) {
        mContext = context;
        mImagesPath = imagesPath;
    }

    @Override
    public int getCount() {
        return mImagesPath.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            // if it's not a recycled view, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0,0,0,0);
        } else {
            imageView = (ImageView) convertView;
        }

        // Set image to view
        Utilities.setThumbnailFromWidth(imageView, mImagesPath.get(position), THUMB_LARGE_W);
        return imageView;
    }
}
