package com.wordpress.excelenteadventura.beerjournal.ui.mainActivity

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.wordpress.excelenteadventura.beerjournal.R
import com.wordpress.excelenteadventura.beerjournal.database.Beer
import kotlinx.android.synthetic.main.beer_adaptor_list_item.view.*


/**
 * Adapter for the recycler view that will be used to show the beer summary information on the main
 * page of the app.
 * Created by DLMcAuslan on 1/3/2017.
 */

class BeerListAdapter(val context: Context) : RecyclerView.Adapter<BeerListAdapter.BeerViewHolder>() {

    private var beers: List<Beer> = arrayListOf()

    class BeerViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val beerName: TextView = itemView.list_item_beer_name
        val beerType: TextView = itemView.list_item_type_bitterness
        val brewery: TextView = itemView.list_item_brewery
        val dateView: TextView = itemView.list_item_date
        val percentageView: TextView = itemView.list_item_percentage
        val rating: TextView = itemView.list_item_rating
        val beerImage: ImageView = itemView.beer_list_item_image
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeerViewHolder {
        return BeerViewHolder(LayoutInflater.from(context).inflate(R.layout.beer_adaptor_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: BeerViewHolder, position: Int) {
        if (beers.isNotEmpty()) {
            val beer = beers[position]
            holder.apply {
                beerName.text = beer.name
                val ibuString =  if (beer.bitterness > 0) " - ${beer.bitterness} IBU" else ""
                beerType.text = "${beer.type}${ibuString}"
                brewery.text = "${beer.brewery}, ${beer.country}"
                dateView.text = formatDate(beer.date)
                percentageView.text = if (beer.percentage >= 0) "${beer.percentage}%" else "0.0%"
                rating.text = "${beer.rating/2}/5"
            }

        } else {
            // Covers the case of data not being ready yet.
            holder.beerName.text = "No Beer"
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

    private fun formatDate(date: String): String {
        val dateSplit = date.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return if (dateSplit.size == 2) dateSplit[2] + "/" + (Integer.parseInt(dateSplit[1]) + 1) + "/" + dateSplit[0]
        else date
    }
}

//class BeerCursorAdapter(context: Context, c: Cursor?) : CursorAdapter(context, c, 0) {
//
//    private val LOG_TAG = BeerCursorAdapter::class.java.simpleName
//
//
//    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
//        // Inflate a list item view
//        return LayoutInflater.from(context).inflate(R.layout.beer_adaptor_list_item, parent, false)
//    }
//
//    // Binds the beer data in the current row pointed to by the cursor to the given list item
//    // layout.
//    override fun bindView(view: View, context: Context, cursor: Cursor) {
//        // Find the individual views we want to set the data to.
//        val beerNameTV = view.findViewById(R.id.list_item_beer_name) as TextView
//        val beerTypeTV = view.findViewById(R.id.list_item_type_bitterness) as TextView
//        val breweryTV = view.findViewById(R.id.list_item_brewery) as TextView
//        val dateTV = view.findViewById(R.id.list_item_date) as TextView
//        val percentageTV = view.findViewById(R.id.list_item_percentage) as TextView
//        val ratingTV = view.findViewById(R.id.list_item_rating) as TextView
//        val beerImage = view.findViewById(R.id.beer_list_item_image) as ImageView
//
//        // Find the columns of beer attributes that we're interested in.
//        val beerNameColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_NAME)
//        val beerTypeColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_TYPE)
//        val beerIBUColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_IBU)
//        val breweryNameColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BREWERY_NAME)
//        val countryColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BREWERY_COUNTRY)
//        val dateColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_DATE)
//        val percentageColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_PERCENTAGE)
//        val ratingColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_RATING)
//        val imageColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_PHOTO)
//
//        // Read the beer attributes from the cursor for the current beer
//        val beerName = cursor.getString(beerNameColumn)
//        val IBU = cursor.getString(beerIBUColumn)
//        var beerType = cursor.getString(beerTypeColumn)
//        if (IBU != "-1") beerType += " - $IBU IBU"
//        val brewery = cursor.getString(breweryNameColumn) + ", " + cursor.getString(countryColumn)
//        val date = formatDate(cursor.getString(dateColumn))
//        var percentValue: Double = cursor.getDouble(percentageColumn)
//        if (percentValue < 0) {
//            percentValue = 0.0
//        }
//        val percentage = percentValue.toString() + "%"
//        val rating = (cursor.getInt(ratingColumn).toDouble() / 2).toString() + "/5"
//        val imageStrings = cursor.getString(imageColumn)
//        val photoPaths = Utilities.stringToList(imageStrings)
//
//        // TODO add some code to make things look nicer for form fields that haven't been filled out.
//
//        // Update the views with the attributes for the current beer
//        beerNameTV.text = beerName
//        beerTypeTV.text = beerType
//        breweryTV.text = brewery
//        dateTV.text = date
//        percentageTV.text = percentage
//        ratingTV.text = rating
//        // Update the image view
////        Utilities.setThumbnailFromWidth(beerImage, photoPaths[0], Companion.getTHUMB_SMALL_W())
//    }
//
//}
