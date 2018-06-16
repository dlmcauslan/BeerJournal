package com.wordpress.excelenteadventura.beerjournal.ui.imagesActivity


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.webkit.MimeTypeMap.getFileExtensionFromUrl
import android.widget.AdapterView
import com.wordpress.excelenteadventura.beerjournal.R
import com.wordpress.excelenteadventura.beerjournal.ui.addBeerActivity.BEER_NAME_EXTRA
import com.wordpress.excelenteadventura.beerjournal.ui.addBeerActivity.PHOTOS_EXTRA
import kotlinx.android.synthetic.main.fragment_images.view.*
import java.io.File


/**
 * Fragment containing a gridview that displays all of the photos of the beer that the user has
 * taken.
 * Created by DLMcAuslan on 5/1/2017.
 */
class ImagesFragment : Fragment() {

    val LOG_TAG = ImagesFragment::class.java.simpleName

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val imageFragment = inflater!!.inflate(R.layout.fragment_images, container, false)

        // Get imagesPaths data from intent.
        val intent = activity.intent
        val imagesPath = intent.getStringArrayListExtra(PHOTOS_EXTRA)
        val beerName = intent.getStringExtra(BEER_NAME_EXTRA)

        // Set title of activity to the beer Name
        activity.title = beerName

        // Find the grid view
        val gridView = imageFragment.images_grid_view
        gridView.adapter = ImageAdapter(activity, imagesPath)

        // Set on item click listener to open gallery for that picture using intent
        gridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            // Opens image in gallery
            val file = File(imagesPath[position])
            val uri = Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_VIEW)
            var mime: String? = "*/*"
            val mimeTypeMap = MimeTypeMap.getSingleton()
            if (mimeTypeMap.hasExtension(getFileExtensionFromUrl(uri.toString()))) {
                mime = mimeTypeMap.getMimeTypeFromExtension(getFileExtensionFromUrl(uri.toString()))
            }
            val apkUri = FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", file)
            intent.setDataAndType(apkUri, mime)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        }
        return imageFragment
    }
}
