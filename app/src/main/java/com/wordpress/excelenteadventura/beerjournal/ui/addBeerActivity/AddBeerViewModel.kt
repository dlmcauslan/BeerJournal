package com.wordpress.excelenteadventura.beerjournal.ui.addBeerActivity

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.wordpress.excelenteadventura.beerjournal.BeerRepository
import com.wordpress.excelenteadventura.beerjournal.Utilities
import com.wordpress.excelenteadventura.beerjournal.database.Beer
import com.wordpress.excelenteadventura.beerjournal.ui.mainActivity.THUMB_LARGE_W
import com.wordpress.excelenteadventura.beerjournal.ui.mainActivity.THUMB_SMALL_W
import java.io.File

class AddBeerViewModel(private val repository: BeerRepository): ViewModel() {

    private val LOG_TAG = AddBeerActivity::class.java.simpleName

    val currentBeer: LiveData<Beer?> = repository.currentBeer

    fun saveBeer(beer: Beer) {
        if (beer.id == null) {
            repository.insertBeer(beer)
        } else {
            repository.updateBeer(beer)
        }
    }

    fun deleteBeer(beer: Beer) {
        for (fileName in Utilities.stringToList(beer.photoLocation)) {
            // Delete image
            val imageFile = File(fileName)
            val deleteSuccessful = imageFile.delete()
            if (deleteSuccessful)
                Log.d(LOG_TAG, "Delete successful: $fileName")
            else
                Log.d(LOG_TAG, "Delete failed: $fileName")
            // Delete small thumbnail
            val thumbFileName = Utilities.thumbFilePath(fileName, THUMB_SMALL_W)
            val thumbFile = File(thumbFileName)
            val thumbDeleteSuccessful = thumbFile.delete()
            if (thumbDeleteSuccessful)
                Log.d(LOG_TAG, "Thumbnail delete successful: $thumbFileName")
            else
                Log.d(LOG_TAG, "Thumbnail delete failed: $thumbFileName")
            // Delete large thumbnail
            val thumbLargeFileName = Utilities.thumbFilePath(fileName, THUMB_LARGE_W)
            val thumbLargeFile = File(thumbLargeFileName)
            val thumbLargeDeleteSuccessful = thumbFile.delete()
            if (thumbLargeDeleteSuccessful)
                Log.d(LOG_TAG, "Thumbnail delete successful: $thumbLargeFile")
            else
                Log.d(LOG_TAG, "Thumbnail delete failed: $thumbLargeFileName")
        }
        repository.deleteBeer(beer)
    }
}