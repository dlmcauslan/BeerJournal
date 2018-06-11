package com.wordpress.excelenteadventura.beerjournal.ui.mainActivity

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.wordpress.excelenteadventura.beerjournal.R
import com.wordpress.excelenteadventura.beerjournal.Utilities
import com.wordpress.excelenteadventura.beerjournal.database.Beer
import kotlinx.android.synthetic.main.beer_adaptor_list_item.view.*


/**
 * Adapter for the recycler view that will be used to show the beer summary information on the main
 * page of the app.
 * Created by DLMcAuslan on 1/3/2017.
 */

class BeerListAdapter(val context: Context, private val listener: OnItemClickListener) : RecyclerView.Adapter<BeerListAdapter.BeerViewHolder>() {

    private var beers: List<Beer> = arrayListOf()

    interface OnItemClickListener {
        fun onItemClick(item: Beer)
    }

    class BeerViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val context: Context = itemView.context
        private val beerName: TextView = itemView.list_item_beer_name
        private val beerType: TextView = itemView.list_item_type_bitterness
        private val brewery: TextView = itemView.list_item_brewery
        private val dateView: TextView = itemView.list_item_date
        private val percentageView: TextView = itemView.list_item_percentage
        private val rating: TextView = itemView.list_item_rating
        private val beerImage: ImageView = itemView.beer_list_item_image

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun bind(beer: Beer, listener: OnItemClickListener) {
            beerName.text = beer.name
            beerType.text = context.getString(R.string.adapter_beer_type, formatEmptyString(beer.type), formatIBUString(beer.bitterness))
            brewery.text = context.getString(R.string.adapter_brewery, formatEmptyString(beer.brewery), formatEmptyString(beer.country))
            dateView.text = formatDate(beer.date)
            percentageView.text = if (beer.percentage >= 0) "${beer.percentage}%" else "0.0%"
            rating.text = context.getString(R.string.adapter_rating, beer.rating.toFloat()/2)
            // Update the image view
            val photoPaths = Utilities.stringToList(beer.photoLocation)
            if (photoPaths.isNotEmpty()) {
                Utilities.setThumbnailFromWidth(beerImage, photoPaths[0], THUMB_SMALL_W)
            } else {
                beerImage.setImageDrawable(context.getDrawable(R.drawable.ic_default_image_foreground))
            }
            itemView.setOnClickListener { listener.onItemClick(beer) }
        }

        private fun formatIBUString(bitterness: Int): String {
            return if (bitterness > 0) " - $bitterness IBU" else ""
        }

        private fun formatEmptyString(string: String): String {
            return if (string.isEmpty()) context.getString(R.string.default_field_string) else string
        }

        private fun formatDate(date: String): String {
            val dateSplit = date.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return if (dateSplit.size == 3) dateSplit[2] + "/" + (Integer.parseInt(dateSplit[1]) + 1) + "/" + dateSplit[0]
            else date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeerViewHolder {
        return BeerViewHolder(LayoutInflater.from(context).inflate(R.layout.beer_adaptor_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: BeerViewHolder, position: Int) {
        if (beers.isNotEmpty()) {
            holder.bind(beers[position], listener)
        }
    }

    fun setBeers(beers: List<Beer>) {
        this.beers = beers
        Log.d("BeerListAdapter", "BeerList, size ${beers.size}: $beers")
        notifyDataSetChanged()
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    override fun getItemCount(): Int {
        return beers.size
    }
}