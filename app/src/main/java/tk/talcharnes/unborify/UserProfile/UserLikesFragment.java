package tk.talcharnes.unborify.UserProfile;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import tk.talcharnes.unborify.Utilities.DatabaseContants;
import tk.talcharnes.unborify.Utilities.SimpleDividerItemDecoration;

/**
 * Created by Khuram Chaudhry on 10/19/17.
 * This fragment displays the photos the user likes.
 */

public class UserLikesFragment extends Fragment {

    private static final String TAG = UserLikesFragment.class.getSimpleName();

    private RecyclerView my_recycler_view;
    private TextView noImageView;

    /**
     * Initializes basic initialization of components of the fragment.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_likes, container, false);

        my_recycler_view = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        noImageView = (TextView) rootView.findViewById(R.id.noImagesTitle);

        final int size = 300;

        my_recycler_view.addItemDecoration(new SimpleDividerItemDecoration(getResources()));
        if (getArguments() != null) {
            String uid = getArguments().getString("uid");
            if (uid != null && !uid.isEmpty()) {
                Log.d(TAG, "Loading user Photos");

                DatabaseContants.getVotesRef().orderByChild(uid).equalTo("likes")
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
                                    my_recycler_view.setLayoutManager(new GridLayoutManager(
                                            getActivity(), size));
                                    my_recycler_view.setHasFixedSize(false);
                                    UserProfileAdapter adapter = new UserProfileAdapter(
                                            getActivity(), DatabaseContants.getCurrentUser()
                                            .getUid(), urls, true);
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