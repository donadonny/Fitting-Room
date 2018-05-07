package tk.talcharnes.unborify.OtherFragmentActivities.Favorites;

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
 * This fragment retrieves the list of user's favorite photos.
 */

public class FavoritesFragment extends Fragment {

    private static final String TAG = FavoritesFragment.class.getSimpleName();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_favorites, container, false);
        InfinitePlaceHolderView mLoadMoreView = (InfinitePlaceHolderView) rootview.findViewById(R.id.loadMoreView);

        mLoadMoreView.getBuilder().setHasFixedSize(false).setItemViewCacheSize(10)
                .setLayoutManager(new GridLayoutManager(getActivity(), 2));

        DatabaseContants.retrievePhotosFromDatabaseUsingUrl(TAG, getActivity(), rootview,
                DatabaseContants.getFavoritesRef(DatabaseContants.getCurrentUser().getUid()),
                mLoadMoreView, R.string.no_image_title_10,false);

        return rootview;
    }
}
