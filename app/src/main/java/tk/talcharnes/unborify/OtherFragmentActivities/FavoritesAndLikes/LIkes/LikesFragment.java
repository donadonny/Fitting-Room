package tk.talcharnes.unborify.OtherFragmentActivities.FavoritesAndLikes.LIkes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import tk.talcharnes.unborify.OtherFragmentActivities.FavoritesAndLikes.HorizontalPagerAdapter;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.FirebaseConstants;

/**
 * Created by khuramchaudhry on 10/22/17.
 */

public class LikesFragment extends Fragment {

    private static final String TAG = LikesFragment.class.getSimpleName();

    private String[] resId = {};
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_likes, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final HorizontalInfiniteCycleViewPager horizontalInfiniteCycleViewPager =
                (HorizontalInfiniteCycleViewPager) view.findViewById(R.id.hicvp);

        Query query = FirebaseConstants.getRef().child(FirebaseConstants.PHOTOS)
                .orderByChild("votes/" + FirebaseConstants.getUser().getUid()).equalTo("likes");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> urls = new ArrayList<String>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    urls.add(snapshot.getKey());
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
