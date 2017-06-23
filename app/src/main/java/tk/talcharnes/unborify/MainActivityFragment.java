    package tk.talcharnes.unborify;

    import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;

    /**
     * A placeholder fragment containing a simple view.
     */
    public class MainActivityFragment extends Fragment {
        FloatingActionButton fab;
        SwipeFlingAdapterView swipeFlingAdapterView;
        ArrayList<String> al;
        ArrayAdapter<String> arrayAdapter;
        private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
        private AdView mAdView;
        private AdRequest mAdRequest;
        private int i = 0;

        public MainActivityFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);


            // The following code is a test

            swipeFlingAdapterView = (SwipeFlingAdapterView) rootView.findViewById(R.id.frame);
            // add entertaining things to arraylist using al.add()
            al = new ArrayList<String>();
            al.add("1");
            al.add("2");
            //choose your favorite adapter
            arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.swipe_layout, R.id.helloText, al);





            //set the listener and the adapter
            swipeFlingAdapterView.setAdapter(arrayAdapter);


            swipeFlingAdapterView.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
                @Override
                public void removeFirstObjectInAdapter() {
                    // this is the simplest way to delete an object from the Adapter (/AdapterView)
                    Log.d("LIST", "removed object!");
                    al.remove(0);
                    arrayAdapter.notifyDataSetChanged();
                }

                @Override
                public void onLeftCardExit(Object dataObject) {
                    //Do something on the left!
                    //You also have access to the original object.
                    //If you want to use it just cast it (String) dataObject
                    Log.d(LOG_TAG, "Left card Exit");
                }

                @Override
                public void onRightCardExit(Object dataObject) {
                    Log.d(LOG_TAG, "Right card Exit");
                }

                @Override
                public void onAdapterAboutToEmpty(int itemsInAdapter) {
                    // Ask for more data here
                    al.add("item number "+ i);
                    i++;
                    arrayAdapter.notifyDataSetChanged();
                    Log.d("LIST", "notified");
                }

                @Override
                public void onScroll(float v) {
                    View view = swipeFlingAdapterView.getSelectedView();
                    view.findViewById(R.id.item_swipe_right_indicator).setAlpha(v < 0 ? -v : 0);
                    view.findViewById(R.id.item_swipe_left_indicator).setAlpha(v > 0 ? v : 0);
                }
            });

            // Optionally add an OnItemClickListener
            swipeFlingAdapterView.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
                @Override
                public void onItemClicked(int itemPosition, Object dataObject) {
                    Log.d(LOG_TAG, "Item clicked");
                }
            });
    //        Test over

            ////        Load ad
            mAdView = (AdView) rootView.findViewById(R.id.adView);
            mAdRequest = new AdRequest.Builder().build();
            mAdView.loadAd(mAdRequest);

            return rootView;
        }


    }
