package tk.talcharnes.unborify.OtherFragmentActivities.LIkes;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mindorks.placeholderview.InfinitePlaceHolderView;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.DatabaseContants;

/**
 * Created by Khuram Chaudhry on 10/22/17.
 * This fragment gets the photos that the user likes.
 */

public class LikesFragment extends Fragment {

    private static final String TAG = LikesFragment.class.getSimpleName();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_likes, container, false);
        InfinitePlaceHolderView mLoadMoreView = (InfinitePlaceHolderView) rootview.findViewById(R.id.loadMoreView);

        mLoadMoreView.getBuilder().setHasFixedSize(false).setItemViewCacheSize(10)
                .setLayoutManager(new GridLayoutManager(getActivity(), 2));

        DatabaseContants.retrievePhotosFromDatabaseUsingUrl(TAG, getActivity(), rootview,
                DatabaseContants.getVotesRef().orderByChild(DatabaseContants.getCurrentUser()
                        .getUid()).equalTo("likes"), mLoadMoreView, R.string.no_image_title_7,
                false);
        return rootview;
    }
}
