package tk.talcharnes.unborify.MainNavigationFragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.listeners.ItemRemovedListener;

import java.util.ArrayList;
import java.util.List;

import tk.talcharnes.unborify.AdCard;
import tk.talcharnes.unborify.Photo;
import tk.talcharnes.unborify.PhotoCard;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.FirebaseConstants;
import tk.talcharnes.unborify.Utilities.Utils;

/**
 * Created by khuramchaudhry on 9/29/17.
 *
 */

public class DealsFragment extends Fragment {

    private static final String TAG = DealsFragment.class.getSimpleName();

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_deals, container, false);

        return rootView;
    }

}