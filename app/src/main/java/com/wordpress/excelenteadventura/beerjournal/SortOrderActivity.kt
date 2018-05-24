package com.wordpress.excelenteadventura.beerjournal

import android.os.Bundle
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment

/**
 * Created by David-local on 1/5/2017.
 */

class SortOrderActivity : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sort_order)
    }

    class UserPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.sort_order)
        }

    }
}
