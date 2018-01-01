package tk.talcharnes.unborify.UserProfile;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.SimpleDividerItemDecoration;
import tk.talcharnes.unborify.Utilities.FirebaseConstants;
import tk.talcharnes.unborify.Utilities.Utils;

/**
 * Created by khuramchaudhry on 10/19/17.
 */

public class UserLikesFragment extends Fragment {

    private static final String TAG = UserLikesFragment.class.getSimpleName();

    private View rootView;
    private RecyclerView my_recycler_view;
    private TextView noImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_user_likes, container, false);

        my_recycler_view = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        noImageView = (TextView) rootView.findViewById(R.id.noImagesTitle);


        Point windowSize = Utils.getDisplaySize(getActivity().getWindowManager());
        final int size = (int) Math.floor(windowSize.x / 300);

        my_recycler_view.addItemDecoration(new SimpleDividerItemDecoration(getResources()));
        if (getArguments() != null) {
            String uid = getArguments().getString("uid");
            if (uid != null && !uid.isEmpty()) {
                Log.d(TAG, "Loading user Photos");

                FirebaseConstants.getRef().child(FirebaseConstants.PHOTOS)
                        .orderByChild("votes/" + uid).equalTo("likes")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ArrayList<String> urls = new ArrayList<String>();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    urls.add(snapshot.getKey());
                                }
                                if (urls.size() < 1) {
                                    noImageView.setVisibility(View.VISIBLE);
                                } else {
                                    noImageView.setVisibility(View.GONE);
                                    my_recycler_view.setLayoutManager(new GridLayoutManager(getActivity(),
                                            size));
                                    my_recycler_view.setHasFixedSize(false);
                                    UserProfileAdapter adapter = new UserProfileAdapter(getActivity(),
                                            FirebaseConstants.getUser().getUid(), urls, "Photos");
                                    my_recycler_view.setAdapter(adapter);
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        }

        return rootView;
    }
}