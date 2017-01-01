package com.wordpress.excelenteadventura.beerjournal;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mFragmentView = inflater.inflate(R.layout.fragment_main, container, false);

        // Setup Floating Action Button to open next activity
        FloatingActionButton fab = (FloatingActionButton) mFragmentView.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                // Only launch the GamesInCommonActivity if at least one friend has been selected.
//                if (!mFriendCompare.isEmpty()) {
                    // Launches AddBeerActivity
                    Intent intent = new Intent(getActivity(), AddBeerActivity.class);
//                    // Set data on the intent to be passed through
//                    intent.putExtra("MainUser", mMainUser);
//                    // TODO: I don't really like how I've done this, some refactoring could be in order.
//                    intent.putStringArrayListExtra("FriendsToFind", new ArrayList<String>(mFriendCompare.keySet()));
                    // Start intent
                    startActivity(intent);
//                } else {
//                    // Otherwise set a toast to alert the user
//                    Toast toast = Toast.makeText(getActivity(), "You must choose at least one friend to compare games with!", Toast.LENGTH_SHORT);
//                    LinearLayout layout = (LinearLayout) toast.getView();
//                    if (layout.getChildCount() > 0) {
//                        TextView tv = (TextView) layout.getChildAt(0);
//                        tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
//                    }
//                    toast.show();
//                }
            }
        });

        return mFragmentView;
    }

}
