package tk.talcharnes.unborify.OtherFragmentActivities.FavoritesAndLikes.Favorites;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tk.talcharnes.unborify.OtherFragmentActivities.FavoritesAndLikes.HorizontalPagerAdapter;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.FirebaseConstants;

/**
 * Created by khuramchaudhry on 10/22/17.
 */

public class FavoritesFragment extends Fragment {

    private static final String TAG = FavoritesFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final HorizontalInfiniteCycleViewPager horizontalInfiniteCycleViewPager =
                (HorizontalInfiniteCycleViewPager) view.findViewById(R.id.hicvp);

        Query query = FirebaseConstants.getRef().child(FirebaseConstants.USERS)
                .child(FirebaseConstants.getUser().getUid())
                .child(FirebaseConstants.USER_FAVORITES);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> urls = new ArrayList<String>();
                GenericTypeIndicator<HashMap<String, String> > t =
                        new GenericTypeIndicator<HashMap<String, String> >() {};
                if(dataSnapshot.exists()) {
                    HashMap<String, String> map = dataSnapshot.getValue(t);
                    if(map != null) {
                        for (Map.Entry<String, String> entry : map.entrySet()) {
                            urls.add(entry.getKey());
                        }
                    }
                }
                horizontalInfiniteCycleViewPager.setAdapter(new HorizontalPagerAdapter(
                        getActivity(), urls));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
