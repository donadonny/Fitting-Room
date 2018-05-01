package tk.talcharnes.unborify.OtherFragmentActivities.About;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import tk.talcharnes.unborify.R;

/**
 * Created by Khuram Chaudhry on 8/31/17.
 * This is the about fragment. Nothing special here.
 */

public class AboutFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }
}