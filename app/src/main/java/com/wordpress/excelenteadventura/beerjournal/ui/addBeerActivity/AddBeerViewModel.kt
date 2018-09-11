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
            deleteImage(fileName)
            // Delete small thumbnail
            val thumbFileName = Utilities.thumbFilePath(fileName, THUMB_SMALL_W)
            deleteImage(thumbFileName)
            // Delete large thumbnail
            val thumbLargeFileName = Utilities.thumbFilePath(fileName, THUMB_LARGE_W)
            deleteImage(thumbLargeFileName)
        }
        repository.deleteBeer(beer)
    }

    private fun deleteImage(fileName: String) {
        val imageFile = File(fileName)
        val deleteSuccessful = imageFile.delete()
        if (deleteSuccessful)
            Log.d(LOG_TAG, "Image delete successful: $fileName")
        else
            Log.d(LOG_TAG, "Image delete failed: $fileName")
    }
}