package com.wordpress.excelenteadventura.beerjournal.ui.mainActivity

import android.content.Context
import com.wordpress.excelenteadventura.beerjournal.R
import com.wordpress.excelenteadventura.beerjournal.database.Beer

class BeerItemViewModel(private val context: Context, beer: Beer) {

    val name = beer.name
    val subtitle: String = context.getString(R.string.adapter_beer_type, formatEmptyString(beer.type), formatIBUString(beer.bitterness))
    val location: String = context.getString(R.string.adapter_brewery, formatEmptyString(beer.brewery), formatEmptyString(beer.country))
    val date = beer.date
    val alcoholContent: String = if (beer.percentage >= 0) "${beer.percentage}%" else "0.0%"
    val rating: String = context.getString(R.string.adapter_rating, formatRating(beer.rating))
    val photoLocation = beer.photoLocation

    private fun formatIBUString(bitterness: Int): String {
        return if (bitterness > 0) " - $bitterness IBU" else ""
    }

    private fun formatEmptyString(string: String): String {
        return if (string.isEmpty()) context.getString(R.string.default_field_string) else string
    }

    private fun formatRating(rating: Int): String {
        val ratingOutOf5 = rating.toFloat()/2
        return if (rating%2 == 1) ratingOutOf5.toString() else ratingOutOf5.toInt().toString()
    }
}