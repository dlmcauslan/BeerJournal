package com.wordpress.excelenteadventura.beerjournal;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.File;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ImagesFragment extends Fragment {

    public static final String LOG_TAG = ImagesFragment.class.getSimpleName();

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
        final ArrayList<String> imagesPath = intent.getStringArrayListExtra("photosExtra");
        String beerName = intent.getStringExtra("beerName");

        // Set title of activity to the beer Name
        getActivity().setTitle(beerName);

        // Find the grid view
        GridView gridView = (GridView) imageFragment.findViewById(R.id.images_grid_view);
        gridView.setAdapter(new ImageAdapter(getActivity(), imagesPath));

        // Set on item click listener to open gallery for that picture using intent
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Opens image in gallery
                Uri uri =  Uri.fromFile(new File(imagesPath.get(position)));
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                String mime = "*/*";
                MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                if (mimeTypeMap.hasExtension(
                        mimeTypeMap.getFileExtensionFromUrl(uri.toString())))
                    mime = mimeTypeMap.getMimeTypeFromExtension(
                            mimeTypeMap.getFileExtensionFromUrl(uri.toString()));
                intent.setDataAndType(uri,mime);
                startActivity(intent);
            }
        });
        return imageFragment;
    }

}
