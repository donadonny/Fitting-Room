package tk.talcharnes.unborify.UserProfile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mindorks.placeholderview.InfinitePlaceHolderView;
import tk.talcharnes.unborify.Models.PhotoModel;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.DatabaseContants;

/**
 * Created by Khuram Chaudhry on 10/19/17.
 * This fragment displays the photos for the user.
 */

public class UserPhotoFragment extends Fragment {

    private static final String TAG = UserPhotoFragment.class.getSimpleName();

    /**
     * Initializes basic initialization of components of the fragment.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_photos, container, false);

        InfinitePlaceHolderView mLoadMoreView = (InfinitePlaceHolderView) rootView
                .findViewById(R.id.loadMoreView);

        mLoadMoreView.getBuilder().setHasFixedSize(false).setItemViewCacheSize(10)
                .setLayoutManager(new LinearLayoutManager(getActivity(),
                        LinearLayoutManager.HORIZONTAL, false));

        if (getArguments() != null) {
            final String uid = getArguments().getString("uid");
            if (uid != null && !uid.isEmpty()) {
                DatabaseContants.retrievePhotosFromDatabaseUsingUrl(TAG, getActivity(), rootView,
                        DatabaseContants.getPhotoRef().orderByChild(PhotoModel.USER_KEY)
                                .equalTo(uid), mLoadMoreView, R.string.no_image_title_3, false);
            }
        }

        return rootView;
    }
}