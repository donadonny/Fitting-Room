package tk.talcharnes.unborify.UserProfile;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.DatabaseContants;

/**
 * Created by Khuram Chaudhry on 10/19/17.
 * This fragment displays the users the user is following.
 */

public class UserFollowingFragment extends Fragment {

    private static final String TAG = UserFollowingFragment.class.getSimpleName();

    private RecyclerView my_recycler_view;
    private TextView noImageView;

    /**
     * Initializes basic initialization of components of the fragment.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_user_following, container, false);

        Log.d(TAG, "Loading UserFollowingFragment");

        my_recycler_view = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        noImageView = (TextView) rootView.findViewById(R.id.noImagesTitle);

        final int size = 2;

        if (getArguments() != null) {
            final String uid = getArguments().getString("uid");
            if (uid != null && !uid.isEmpty()) {
                Log.d(TAG, "Loading user Photos");

                DatabaseContants.getFollowingRef().child(uid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ArrayList<String> users = new ArrayList<String>();
                                GenericTypeIndicator<HashMap<String, String>> t =
                                        new GenericTypeIndicator<HashMap<String, String>>() {
                                        };
                                if (dataSnapshot.exists()) {
                                    HashMap<String, String> map = dataSnapshot.getValue(t);
                                    if (map != null) {
                                        for (Map.Entry<String, String> entry : map.entrySet()) {
                                            if (entry.getValue().equals("Following")) {
                                                users.add(entry.getKey());
                                            }
                                        }
                                    }
                                }
                                if (users.size() < 1) {
                                    noImageView.setVisibility(View.VISIBLE);
                                } else {
                                    noImageView.setVisibility(View.GONE);
                                    my_recycler_view.setLayoutManager(new GridLayoutManager(
                                            getActivity(), size));
                                    my_recycler_view.setHasFixedSize(false);
                                    UserProfileAdapter adapter = new UserProfileAdapter(
                                            getActivity(), uid, DatabaseContants.getCurrentUser()
                                            .getUid(), users, false);
                                    my_recycler_view.setAdapter(adapter);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d(TAG, databaseError.getMessage());
                            }
                        });
            }
        }

        return rootView;
    }
}