package com.wordpress.excelenteadventura.beerjournal.ui.mainActivity

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.wordpress.excelenteadventura.beerjournal.R
import com.wordpress.excelenteadventura.beerjournal.Utilities
import com.wordpress.excelenteadventura.beerjournal.database.Beer
import com.wordpress.excelenteadventura.beerjournal.databinding.BeerAdaptorListItemBinding
import com.wordpress.excelenteadventura.beerjournal.visible
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

    inner class BeerViewHolder (private val binding: BeerAdaptorListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val beerImage: ImageView = itemView.beer_list_item_image
        private val defaultImage = itemView.beer_list_item_image_default

        fun bind(beer: BeerItemViewModel, position: Int, listener: OnItemClickListener) {
            binding.beer = beer
            // Update the image view
            val photoPaths = Utilities.stringToList(beer.photoLocation)
            val photoExists = photoPaths.isNotEmpty()
            beerImage.visible(photoExists)
            defaultImage.visible(!photoExists)
            if (photoExists) Utilities.setThumbnailFromWidth(beerImage, photoPaths[0], THUMB_SMALL_W)
            itemView.setOnClickListener { listener.onItemClick(beers[position]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeerViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = BeerAdaptorListItemBinding.inflate(inflater, parent, false)
        return BeerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BeerViewHolder, position: Int) {
        if (beers.isNotEmpty()) {
            holder.bind(BeerItemViewModel(context, beers[position]), position, listener)
        }
    }

    fun setBeers(beers: List<Beer>) {
        this.beers = beers
        Log.d("BeerListAdapter", "BeerList, size ${beers.size}: $beers")
    }

    fun sortBeers(sortType: String, sortDirection: Boolean) {
        beers = when (sortType) {
            context.getString(R.string.sort_beer_type) -> beers.sortedWith(compareBy(Beer::type, Beer::name))
            context.getString(R.string.sort_beer_percentage) -> beers.sortedWith(compareBy(Beer::percentage, Beer::name))
            context.getString(R.string.sort_beer_rating) -> beers.sortedWith(compareBy(Beer::rating, Beer::name))
            context.getString(R.string.sort_beer_date) -> beers.sortedWith(compareBy(Beer::date, Beer::name))
            context.getString(R.string.sort_beer_bitterness) -> beers.sortedWith(compareBy(Beer::bitterness, Beer::name))
            context.getString(R.string.sort_beer_location) -> beers.sortedWith(compareBy<Beer> { it.country }.thenBy { it.brewery }.thenBy { it.name })
            else -> beers.sortedBy { it.name }
        }
        if (!sortDirection) beers = beers.asReversed()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return beers.size
    }
}