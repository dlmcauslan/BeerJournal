package com.wordpress.excelenteadventura.beerjournal;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ImagesFragment extends Fragment {


    public ImagesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View imageFragment = inflater.inflate(R.layout.fragment_images, container, false);

        // Get imagesPaths data from intent.
        Intent intent = getActivity().getIntent();
        ArrayList<String> imagesPath = intent.getStringArrayListExtra("photosExtra");

        // Find the grid view
        GridView gridView = (GridView) imageFragment.findViewById(R.id.images_grid_view);
        gridView.setAdapter(new ImageAdapter(getActivity(), imagesPath));

        // Set on item click listener to open gallery for that picture using intent
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Add intent to open image in gallery here
            }
        });
        return imageFragment;
    }

}
