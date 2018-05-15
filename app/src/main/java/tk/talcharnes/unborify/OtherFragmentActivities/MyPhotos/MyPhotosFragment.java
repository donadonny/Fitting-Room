package tk.talcharnes.unborify.OtherFragmentActivities.MyPhotos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.auth.FirebaseUser;
import com.mindorks.placeholderview.InfinitePlaceHolderView;
import tk.talcharnes.unborify.Models.PhotoModel;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.DatabaseContants;

/**
 * Created by Khuram Chaudhry on 9/29/17.
 * This fragment gets the user's photos from the database.
 */
public class MyPhotosFragment extends Fragment {

    private static final String LOG_TAG = MyPhotosFragment.class.getSimpleName();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_my_photos, container, false);
        InfinitePlaceHolderView mLoadMoreView = (InfinitePlaceHolderView) rootview.findViewById(R.id.loadMoreView);

        mLoadMoreView.getBuilder().setHasFixedSize(false).setItemViewCacheSize(10)
                .setLayoutManager(new GridLayoutManager(getActivity(), 2));

        FirebaseUser user = DatabaseContants.getCurrentUser();
        String userId = user.getUid();

        DatabaseContants.retrievePhotosFromDatabase(LOG_TAG, getActivity(), rootview,
                DatabaseContants.getPhotoRef().orderByChild(PhotoModel.USER_KEY).equalTo(userId),
                mLoadMoreView, R.string.no_image_title_9,true);

        return rootview;
    }
}
