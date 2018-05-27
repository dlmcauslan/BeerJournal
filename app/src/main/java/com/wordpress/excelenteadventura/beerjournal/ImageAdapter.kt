package com.wordpress.excelenteadventura.beerjournal

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import java.util.*

/**
 * Adaptor class that sets images to the gridview in the ImagesFragment.
 * Created by DLMcAuslan on 1/5/2017.
 */

class ImageAdapter(private val mContext: Context, private val mImagesPath: ArrayList<String>) : BaseAdapter() {

    override fun getCount(): Int {
        return mImagesPath.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val imageView: ImageView

        if (convertView == null) {
            // if it's not a recycled view, initialize some attributes
            imageView = ImageView(mContext)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setPadding(0, 0, 0, 0)
        } else {
            imageView = convertView as ImageView
        }

        // Set image to view
//        Utilities.setThumbnailFromWidth(imageView, mImagesPath[position], Companion.getTHUMB_LARGE_W())
        return imageView
    }

    companion object {

        private val LOG_TAG = ImageAdapter::class.java.simpleName
    }
}
