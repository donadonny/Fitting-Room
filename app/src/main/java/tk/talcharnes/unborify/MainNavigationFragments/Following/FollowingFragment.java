package tk.talcharnes.unborify.MainNavigationFragments.Following;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.DatabaseContants;

/**
 * Created by Khuram Chaudhry on 9/29/17.
 *
 * This fragment displays the users the main user is following. The display includes photos,
 *      profile images, and the names of the users.
 */


public class FollowingFragment extends Fragment {

    private static final String TAG = FollowingFragment.class.getSimpleName();

    private RecyclerView my_recycler_view;
    private TextView noImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_following, container, false);
        my_recycler_view = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        noImageView = (TextView) rootView.findViewById(R.id.noImagesTitle);

        Log.d(TAG, "Load Following Fragment");

        initialize();

        return rootView;
    }

    /**
     * This function grabs the uids of the users the main user is following and sends the list to
     *      the following adapter.
     */
    private void initialize() {
        // uid is the user id of the logged in user.
        String uid = DatabaseContants.getCurrentUser().getUid();
        if (!uid.isEmpty()) {
            Log.d(TAG, "Loading user Photos");

            // Grabbing all the uids of the users from following(db reference) and sends the list
            //      to the followingAdapter if the list isn't empty else it will display a
            //      message instead.
            DatabaseContants.getUserFollowingRef().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<String> users = new ArrayList<String>();

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String userConnection = snapshot.getValue(String.class);
                        if(userConnection != null && userConnection.equals("Following"))
                        users.add(snapshot.getKey());
                    }
                    // Checks if the list isn't empty and set noImageView visible which displays
                    //      a message telling user to following other users.
                    if (users.size() < 1) {
                        noImageView.setVisibility(View.VISIBLE);
                    } else {
                        noImageView.setVisibility(View.GONE);
                        my_recycler_view.setLayoutManager(new
                                LinearLayoutManager(getActivity(),
                                LinearLayoutManager.VERTICAL, false));

                        my_recycler_view.setHasFixedSize(false);
                        FollowingAdapter adapter = new FollowingAdapter(
                                getActivity(), users);
                        my_recycler_view.setAdapter(adapter);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    DatabaseContants.logDatabaseError(TAG, databaseError);
                }
            });

        }

    }

}