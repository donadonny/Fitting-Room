package tk.talcharnes.unborify.my_photos;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tk.talcharnes.unborify.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MyPhotosFragment extends Fragment {

    public MyPhotosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_my_photos, container, false);



        return rootview;
    }
}
