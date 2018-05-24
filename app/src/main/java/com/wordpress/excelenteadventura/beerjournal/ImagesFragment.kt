package com.wordpress.excelenteadventura.beerjournal


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.AdapterView
import android.widget.GridView

import java.io.File
import java.util.ArrayList


/**
 * Fragment containing a gridview that displays all of the photos of the beer that the user has
 * taken.
 * Created by DLMcAuslan on 5/1/2017.
 */
class ImagesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val imageFragment = inflater!!.inflate(R.layout.fragment_images, container, false)

        // Get imagesPaths data from intent.
        val intent = activity.intent
        val imagesPath = intent.getStringArrayListExtra("photosExtra")
        val beerName = intent.getStringExtra("beerName")

        // Set title of activity to the beer Name
        activity.title = beerName

        // Find the grid view
        val gridView = imageFragment.findViewById(R.id.images_grid_view) as GridView
        gridView.adapter = ImageAdapter(activity, imagesPath)

        // Set on item click listener to open gallery for that picture using intent
        gridView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // Opens image in gallery
            val uri = Uri.fromFile(File(imagesPath[position]))
            val intent = Intent(android.content.Intent.ACTION_VIEW)
            var mime: String? = "*/*"
            val mimeTypeMap = MimeTypeMap.getSingleton()
            if (mimeTypeMap.hasExtension(
                            mimeTypeMap.getFileExtensionFromUrl(uri.toString())))
                mime = mimeTypeMap.getMimeTypeFromExtension(
                        mimeTypeMap.getFileExtensionFromUrl(uri.toString()))
            intent.setDataAndType(uri, mime)
            startActivity(intent)
        }
        return imageFragment
    }

    companion object {

        val LOG_TAG = ImagesFragment::class.java.simpleName
    }

}// Required empty public constructor
