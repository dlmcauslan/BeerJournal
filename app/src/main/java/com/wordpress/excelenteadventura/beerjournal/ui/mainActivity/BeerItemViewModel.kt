package com.wordpress.excelenteadventura.beerjournal.ui.mainActivity

import android.content.Context
import com.wordpress.excelenteadventura.beerjournal.R
import com.wordpress.excelenteadventura.beerjournal.database.Beer

data class BeerItemViewModel(val name: String,
                             val subtitle: String,
                             val location: String,
                             val date: String,
                             val alcoholContent: String,
                             val rating: String,
                             val photoLocation: String)

fun Beer.toAdapterViewModel(context: Context): BeerItemViewModel {
    val defaultFieldString = context.getString(R.string.default_field_string)
    return BeerItemViewModel(
            name,
            context.getString(R.string.adapter_beer_type, formatEmptyString(type, defaultFieldString), formatIBUString(bitterness)),
            context.getString(R.string.adapter_brewery, formatEmptyString(brewery, defaultFieldString), formatEmptyString(country, defaultFieldString)),
            date,
            if (percentage >= 0) "$percentage%" else "0.0%",
            context.getString(R.string.adapter_rating, formatRating(rating)),
            photoLocation
    )
}

private fun formatIBUString(bitterness: Int): String {
    return if (bitterness > 0) " - $bitterness IBU" else ""
}

private fun formatEmptyString(string: String, defaultString: String): String {
    return if (string.isEmpty()) defaultString else string
}

private fun formatRating(rating: Int): String {
    val ratingOutOf5 = rating.toFloat()/2
    return if (rating%2 == 1) ratingOutOf5.toString() else ratingOutOf5.toInt().toString()
}