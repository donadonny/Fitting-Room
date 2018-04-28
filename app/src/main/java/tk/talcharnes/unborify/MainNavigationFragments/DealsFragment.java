package tk.talcharnes.unborify.MainNavigationFragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tk.talcharnes.unborify.R;

/**
 * Created by khuramchaudhry on 9/29/17.
 * This fragment displays the deals screen.
 */

public class DealsFragment extends Fragment {

    private static final String TAG = DealsFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_deals, container, false);

        return rootView;
    }

}