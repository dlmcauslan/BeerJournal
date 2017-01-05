package com.wordpress.excelenteadventura.beerjournal;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import static com.wordpress.excelenteadventura.beerjournal.MainFragment.THUMB_LARGE_W;

/**
 * Created by David-local on 1/5/2017.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> mImagesPath;

    public ImageAdapter(Context context, ArrayList<String> imagesPath) {
        mContext = context;
        mImagesPath = imagesPath;
    }

    @Override
    public int getCount() {
        return 0;
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
        } else {
            imageView = (ImageView) convertView;
        }

        // Set image to view
        Utilities.setThumbnailFromWidth(imageView, mImagesPath.get(position), THUMB_LARGE_W);
//        Bitmap bitmap = decodeFile(mImagesPath.get(position));
//        imageView.setImageBitmap(bitmap);
        return imageView;
    }
}
