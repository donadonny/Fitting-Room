package tk.talcharnes.unborify.UserProfile;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.DatabaseContants;

/**
 * Created by Khuram Chaudhry on 10/19/17.
 * This fragment displays the users the user is following.
 */

public class UserFollowingFragment extends Fragment {

    private static final String TAG = UserFollowingFragment.class.getSimpleName();

    /**
     * Initializes basic initialization of components of the fragment.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_user_following, container, false);

        RecyclerView my_recycler_view = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);

        if (getArguments() != null) {
            final String uid = getArguments().getString("uid");
            if (uid != null && !uid.isEmpty()) {
                DatabaseContants.retriveProfilePhotosFromDatabase(TAG, getActivity(), rootView,
                        DatabaseContants.getFollowingRef().child(uid),
                        my_recycler_view, R.string.no_image_title_4);
            }
        }

        return rootView;
    }
}