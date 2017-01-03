package com.wordpress.excelenteadventura.beerjournal;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wordpress.excelenteadventura.beerjournal.database.BeerContract.BeerEntry;

/**
 * Adapter for the list view that will be used to show the beer summary information on the main
 * page of the app.
 * Created by DLMcAuslan on 1/3/2017.
 */

public class BeerCursorAdapter extends CursorAdapter{

    public BeerCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view
        return LayoutInflater.from(context).inflate(R.layout.beer_adaptor_list_item, parent, false);
    }

    // Binds the beer data in the current row pointed to by the cursor to the given list item
    // layout.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find the individual views we want to set the data to.
        TextView beerNameTV = (TextView) view.findViewById(R.id.list_item_beer_name);
        TextView beerTypeTV = (TextView) view.findViewById(R.id.list_item_type_bitterness);
        TextView breweryTV = (TextView) view.findViewById(R.id.list_item_brewery);
        TextView dateTV = (TextView) view.findViewById(R.id.list_item_date);
        TextView percentageTV = (TextView) view.findViewById(R.id.list_item_percentage);
        TextView ratingTV = (TextView) view.findViewById(R.id.list_item_rating);
        ImageView beerImage = (ImageView) view.findViewById(R.id.list_item_image);

        // Find the columns of beer attributes that we're interested in.
        int beerNameColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_NAME);
        int beerTypeColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_TYPE);
        int beerIBUColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_IBU);
        int breweryNameColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BREWERY_NAME);
        int countryColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BREWERY_COUNTRY);
        int dateColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_DATE);
        int percentageColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_PERCENTAGE);
        int ratingColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_RATING);
        int imageColumn = cursor.getColumnIndex(BeerEntry.COLUMN_BEER_PHOTO);

        // Read the beer attributes from the cursor for the current beer
        String beerName = cursor.getString(beerNameColumn);
        String beerType = cursor.getString(beerTypeColumn) + " - " + cursor.getString(beerIBUColumn) + " IBU";
        String brewery = cursor.getString(breweryNameColumn) + ", " + cursor.getString(countryColumn);
        String date = cursor.getString(dateColumn);
        String percentage = cursor.getDouble(percentageColumn) + "%";
        String rating = ((double)cursor.getInt(ratingColumn)/2) + "/5";
        //TODO get image

        // TODO add some code to make things look nicer for form fields that haven't been filled out.

        // Update the views with the attributes for the current beer
        beerNameTV.setText(beerName);
        beerTypeTV.setText(beerType);
        breweryTV.setText(brewery);
        dateTV.setText(date);
        percentageTV.setText(percentage);
        ratingTV.setText(rating);
        // TODO set image
    }
}
