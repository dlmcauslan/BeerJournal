package com.wordpress.excelenteadventura.beerjournal.ui.imagesActivity

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.wordpress.excelenteadventura.beerjournal.Utilities
import com.wordpress.excelenteadventura.beerjournal.ui.mainActivity.THUMB_LARGE_W
import java.util.*

/**
 * Adaptor class that sets images to the gridview in the ImagesFragment.
 * Created by DLMcAuslan on 1/5/2017.
 */

class ImageAdapter(private val context: Context, private val imagesPath: ArrayList<String>) : BaseAdapter() {

    override fun getCount(): Int {
        return imagesPath.size
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
            imageView = ImageView(context)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setPadding(0, 0, 0, 0)
        } else {
            imageView = convertView as ImageView
        }

        // Set image to view
        Utilities.setThumbnailFromWidth(imageView, imagesPath[position], THUMB_LARGE_W)
        return imageView
    }

    companion object {

        private val LOG_TAG = ImageAdapter::class.java.simpleName
    }
}
